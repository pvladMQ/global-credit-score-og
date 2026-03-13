package com.tanzu.creditengine.repository;

import com.tanzu.creditengine.entity.CreditScoreCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Valkey/Redis Repository for CreditScoreCache.
 * Provides sub-second access to cached credit scores.
 */
@Repository
public interface CreditScoreCacheRepository extends CrudRepository<CreditScoreCache, String> {

    /**
     * Find cached score by SSN.
     */
    CreditScoreCache findBySsn(String ssn);

    /**
     * Find all cached scores by risk level.
     */
    List<CreditScoreCache> findByRiskLevel(String riskLevel);

    /**
     * Find all cached scores above a threshold.
     */
    List<CreditScoreCache> findByCalculatedScoreGreaterThan(Integer score);
}
