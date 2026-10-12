package com.dmitryefremov.site.agent.dto;

public class AgentOrderRequest {
    private String clientName;
    private String clientEmail;
    private String serviceType;
    private String requirements;

    public AgentOrderRequest() {
    }

    public AgentOrderRequest(String clientName, String clientEmail, String serviceType, String requirements) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.serviceType = serviceType;
        this.requirements = requirements;
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
}
