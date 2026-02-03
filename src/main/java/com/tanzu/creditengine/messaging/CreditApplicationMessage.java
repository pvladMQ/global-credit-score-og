package com.tanzu.creditengine.messaging;

import java.io.Serializable;

/**
 * DTO for credit application messages sent via RabbitMQ.
 */
public class CreditApplicationMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ssn;
    private String fullName;
    private Integer requestedCreditLimit;
    private String applicationReason;

    // Default constructor for JSON deserialization
    public CreditApplicationMessage() {
    }

    public CreditApplicationMessage(String ssn, String fullName, Integer requestedCreditLimit,
            String applicationReason) {
        this.ssn = ssn;
        this.fullName = fullName;
        this.requestedCreditLimit = requestedCreditLimit;
        this.applicationReason = applicationReason;
    }

    // Getters and Setters
    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getRequestedCreditLimit() {
        return requestedCreditLimit;
    }

    public void setRequestedCreditLimit(Integer requestedCreditLimit) {
        this.requestedCreditLimit = requestedCreditLimit;
    }

    public String getApplicationReason() {
        return applicationReason;
    }

    public void setApplicationReason(String applicationReason) {
        this.applicationReason = applicationReason;
    }

    @Override
    public String toString() {
        return "CreditApplicationMessage{" +
                "ssn='" + ssn + '\'' +
                ", fullName='" + fullName + '\'' +
                ", requestedCreditLimit=" + requestedCreditLimit +
                ", applicationReason='" + applicationReason + '\'' +
                '}';
    }
}
