package com.tanzu.creditengine.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Redis/Valkey model for caching calculated credit scores.
 * This enables sub-second global retrieval of credit scores.
 */
@RedisHash("CreditScoreCache")
public class CreditScoreCache implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String ssn;

    private Integer calculatedScore;

    private LocalDateTime calculatedAt;

    private String riskLevel;

    private String fullName;

    // Default constructor
    public CreditScoreCache() {
    }

    public CreditScoreCache(String ssn, Integer calculatedScore, String riskLevel, String fullName) {
        this.ssn = ssn;
        this.calculatedScore = calculatedScore;
        this.riskLevel = riskLevel;
        this.fullName = fullName;
        this.calculatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Integer getCalculatedScore() {
        return calculatedScore;
    }

    public void setCalculatedScore(Integer calculatedScore) {
        this.calculatedScore = calculatedScore;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "CreditScoreCache{" +
                "ssn='" + ssn + '\'' +
                ", calculatedScore=" + calculatedScore +
                ", calculatedAt=" + calculatedAt +
                ", riskLevel='" + riskLevel + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
