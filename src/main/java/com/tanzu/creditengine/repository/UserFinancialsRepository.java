package com.tanzu.creditengine.repository;

import com.tanzu.creditengine.entity.UserFinancials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for UserFinancials entity.
 * Provides data access to PostgreSQL for the "Complex Join" simulation.
 */
@Repository
public interface UserFinancialsRepository extends JpaRepository<UserFinancials, String> {

    /**
     * Find user by SSN.
     */
    Optional<UserFinancials> findBySsn(String ssn);

    /**
     * Simulated "Complex Join" query - finds users with risk assessment data.
     * In a real scenario, this would join multiple tables.
     */
    @Query("SELECT u FROM UserFinancials u WHERE u.ssn = :ssn AND u.creditHistoryScore IS NOT NULL")
    Optional<UserFinancials> findWithCompleteFinancialData(@Param("ssn") String ssn);

    /**
     * Find all users by risk level.
     */
    List<UserFinancials> findByRiskLevel(String riskLevel);

    /**
     * Find all users with criminal records.
     */
    List<UserFinancials> findByCriminalRecordTrue();
}
