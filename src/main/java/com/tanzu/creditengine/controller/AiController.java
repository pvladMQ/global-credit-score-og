package com.tanzu.creditengine.controller;

import com.tanzu.creditengine.entity.CreditScoreCache;
import com.tanzu.creditengine.repository.CreditScoreCacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * REST Controller simulating an AI assistant using local data for demo
 * purposes.
 * This intercepts the frontend requests to /api/ai/query and parses them
 * programmatically
 * to return structured data without requiring a real LLM binding.
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private static final Logger logger = LoggerFactory.getLogger(AiController.class);

    @Autowired
    private CreditScoreCacheRepository cacheRepository;

    @PostMapping("/query")
    public ResponseEntity<?> queryAi(@RequestBody Map<String, String> payload) {
        String prompt = payload.get("prompt");
        logger.info("Received AI Query: {}", prompt);

        if (prompt == null || prompt.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Prompt cannot be empty"));
        }

        String lowerPrompt = prompt.toLowerCase();
        List<Map<String, Object>> results = new ArrayList<>();

        // Fetch all data from cache to simulate natural language SQL filtering
        Iterable<CreditScoreCache> allCachedScores = cacheRepository.findAll();

        // Pattern to look for something like "above 50" or "> 50"
        Pattern greaterThanPattern = Pattern.compile("(above|greater than|>)\\s*(\\d+)");
        Matcher gtMatcher = greaterThanPattern.matcher(lowerPrompt);

        // Pattern to look for something like "below 50" or "< 50"
        Pattern lessThanPattern = Pattern.compile("(below|less than|<)\\s*(\\d+)");
        Matcher ltMatcher = lessThanPattern.matcher(lowerPrompt);

        List<CreditScoreCache> filtered = new ArrayList<>();
        allCachedScores.forEach(filtered::add);

        boolean filteredByScore = false;

        if (gtMatcher.find()) {
            int threshold = Integer.parseInt(gtMatcher.group(2));
            filtered.removeIf(score -> score.getCalculatedScore() == null || score.getCalculatedScore() <= threshold);
            filteredByScore = true;
        } else if (ltMatcher.find()) {
            int threshold = Integer.parseInt(ltMatcher.group(2));
            filtered.removeIf(score -> score.getCalculatedScore() == null || score.getCalculatedScore() >= threshold);
            filteredByScore = true;
        }

        // Basic keyword filtering
        if (lowerPrompt.contains("high risk")) {
            filtered.removeIf(s -> !"HIGH".equalsIgnoreCase(s.getRiskLevel()));
            filteredByScore = true;
        } else if (lowerPrompt.contains("low risk")) {
            filtered.removeIf(s -> !"LOW".equalsIgnoreCase(s.getRiskLevel()));
            filteredByScore = true;
        }

        if (filtered.isEmpty()) {
            return ResponseEntity.ok(Map.of("message",
                    "I couldn't find any users matching your criteria. Try adding more users first."));
        }

        // Map to structured format as expected by the UI table
        for (CreditScoreCache score : filtered) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("SSN (Masked)", maskSsn(score.getSsn()));
            row.put("Full Name", score.getFullName() != null ? score.getFullName() : "--");
            row.put("Calculated Score", score.getCalculatedScore());
            row.put("Risk Level", score.getRiskLevel());
            results.add(row);
        }

        return ResponseEntity.ok(results);
    }

    private String maskSsn(String ssn) {
        if (ssn == null || ssn.length() < 4)
            return "****";
        return "***-**-" + ssn.substring(ssn.length() - 4);
    }
}
