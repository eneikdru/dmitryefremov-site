package com.dmitryefremov.site.compliance.dto;

import java.time.OffsetDateTime;

public class AgentOrderDto {
    private Long id;
    private String userIdentifier;
    private String serviceType;
    private String details;
    private String status;
    private OffsetDateTime createdAt;

    public AgentOrderDto() {
    }

    public AgentOrderDto(Long id, String userIdentifier, String serviceType, String details, String status, OffsetDateTime createdAt) {
        this.id = id;
        this.userIdentifier = userIdentifier;
        this.serviceType = serviceType;
        this.details = details;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
