package com.tanzu.creditengine.bootstrap;

import com.tanzu.creditengine.entity.CreditScoreCache;
import com.tanzu.creditengine.entity.UserFinancials;
import com.tanzu.creditengine.repository.CreditScoreCacheRepository;
import com.tanzu.creditengine.repository.UserFinancialsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private UserFinancialsRepository userFinancialsRepository;

    @Autowired
    private CreditScoreCacheRepository creditScoreCacheRepository;

    @Override
    public void run(String... args) throws Exception {
        loadUserData();
        loadCacheData();
    }

    private void loadUserData() {
        if (userFinancialsRepository.count() == 0) {
            logger.info("Initializing PostgreSQL database with 200 sample UserFinancials records...");
            List<UserFinancials> users = new ArrayList<>();
            Random random = new Random();

            String[] firstNames = { "John", "Jane", "Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace",
                    "Heidi", "Vlad", "Ivan", "Jack" };
            String[] lastNames = { "Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson",
                    "Moore", "Taylor", "Popa", "Anderson" };

            for (int i = 0; i < 200; i++) {
                String ssn = generateRandomSsn(random);
                String fullName = firstNames[random.nextInt(firstNames.length)] + " "
                        + lastNames[random.nextInt(lastNames.length)];
                int creditHistoryScore = 300 + random.nextInt(550); // 300 to 850
                boolean criminalRecord = random.nextDouble() > 0.9; // 10% chance of criminal record

                String riskLevel;
                if (criminalRecord || creditHistoryScore < 500) {
                    riskLevel = "HIGH";
                } else if (creditHistoryScore > 700) {
                    riskLevel = "LOW";
                } else {
                    riskLevel = "MEDIUM";
                }

                users.add(new UserFinancials(ssn, fullName, creditHistoryScore, criminalRecord, riskLevel));
            }

            userFinancialsRepository.saveAll(users);
            logger.info("Successfully loaded 200 records into PostgreSQL.");
        } else {
            logger.info("PostgreSQL database already contains data. Skipping initialization.");
        }
    }

    private void loadCacheData() {
        if (creditScoreCacheRepository.count() == 0) {
            logger.info("Initializing Valkey with 5 sample CreditScoreCache records...");

            // We just fetch the first 5 records from DB to cache
            Iterable<UserFinancials> allUsers = userFinancialsRepository.findAll();
            int count = 0;
            for (UserFinancials user : allUsers) {
                if (count >= 5)
                    break;

                // Create a calculated score and push to cache
                int calculatedScore = user.getCreditHistoryScore() + (user.getCriminalRecord() ? -100 : 50);
                if (calculatedScore > 850)
                    calculatedScore = 850;
                if (calculatedScore < 300)
                    calculatedScore = 300;

                CreditScoreCache cache = new CreditScoreCache(
                        user.getSsn(),
                        calculatedScore,
                        user.getRiskLevel(),
                        user.getFullName());
                creditScoreCacheRepository.save(cache);
                count++;
            }
            logger.info("Successfully loaded 5 records into Valkey.");
        } else {
            logger.info("Valkey already contains cached data. Skipping initialization.");
        }
    }

    private String generateRandomSsn(Random random) {
        return String.format("%03d-%02d-%04d",
                random.nextInt(1000),
                random.nextInt(100),
                random.nextInt(10000));
    }
}
