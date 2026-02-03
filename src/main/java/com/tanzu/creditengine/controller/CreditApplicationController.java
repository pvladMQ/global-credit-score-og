package com.tanzu.creditengine.controller;

import com.tanzu.creditengine.entity.CreditScoreCache;
import com.tanzu.creditengine.messaging.CreditApplicationMessage;
import com.tanzu.creditengine.service.CreditApplicationService;
import com.tanzu.creditengine.service.CreditScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for credit application endpoints.
 */
@RestController
@RequestMapping("/api")
public class CreditApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(CreditApplicationController.class);

    private final CreditApplicationService applicationService;
    private final CreditScoreCalculator scoreCalculator;

    public CreditApplicationController(CreditApplicationService applicationService,
            CreditScoreCalculator scoreCalculator) {
        this.applicationService = applicationService;
        this.scoreCalculator = scoreCalculator;
    }

    /**
     * Submits a credit application for processing.
     * The application is sent to a RabbitMQ queue for asynchronous processing.
     * 
     * POST /apply
     * 
     * @param application The credit application payload
     * @return Confirmation response
     */
    @PostMapping("/apply")
    public ResponseEntity<Map<String, Object>> submitApplication(@RequestBody CreditApplicationMessage application) {
        logger.info("Received credit application request for SSN: {}", application.getSsn());

        // Validate required fields
        if (application.getSsn() == null || application.getSsn().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "SSN is required");
            return ResponseEntity.badRequest().body(error);
        }

        if (application.getFullName() == null || application.getFullName().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Full name is required");
            return ResponseEntity.badRequest().body(error);
        }

        // Submit to RabbitMQ queue for processing
        applicationService.submitApplication(application);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "accepted");
        response.put("message", "Credit application submitted for processing");
        response.put("ssn", application.getSsn());
        response.put("trackingInfo", "Check /api/score/" + application.getSsn() + " for results");

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Retrieves a cached credit score from GemFire.
     * This endpoint demonstrates sub-second global retrieval.
     * 
     * GET /score/{ssn}
     * 
     * @param ssn The SSN to look up
     * @return The cached credit score
     */
    @GetMapping("/score/{ssn}")
    public ResponseEntity<Map<String, Object>> getScore(@PathVariable String ssn) {
        logger.info("Retrieving cached credit score for SSN: {}", ssn);

        CreditScoreCache cachedScore = scoreCalculator.getCachedScore(ssn);

        if (cachedScore == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "not_found");
            response.put("message", "No credit score found for SSN: " + ssn);
            response.put("hint", "Submit an application first via POST /api/apply");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("ssn", cachedScore.getSsn());
        response.put("fullName", cachedScore.getFullName());
        response.put("calculatedScore", cachedScore.getCalculatedScore());
        response.put("riskLevel", cachedScore.getRiskLevel());
        response.put("calculatedAt", cachedScore.getCalculatedAt());
        response.put("source", "GemFire Cache (sub-second retrieval)");

        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Global Credit Scoring Engine");
        return ResponseEntity.ok(response);
    }
}
