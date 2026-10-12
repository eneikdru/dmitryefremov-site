package com.dmitryefremov.site.agent.dto;

import java.time.OffsetDateTime;

public class AgentOrderResponse {
    private Long id;
    private String clientName;
    private String clientEmail;
    private String serviceType;
    private String requirements;
    private String status;
    private OffsetDateTime createdAt;

    public AgentOrderResponse() {
    }

    public AgentOrderResponse(Long id, String clientName, String clientEmail, String serviceType, String requirements, String status, OffsetDateTime createdAt) {
        this.id = id;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.serviceType = serviceType;
        this.requirements = requirements;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
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
