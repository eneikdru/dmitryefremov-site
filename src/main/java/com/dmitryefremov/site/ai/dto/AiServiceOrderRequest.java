package com.dmitryefremov.site.ai.dto;

public class AiServiceOrderRequest {

    private String userName;
    private String userEmail;
    private String agentIdentifier;
    private String serviceType;
    private String requirements;

    public AiServiceOrderRequest() {
    }

    public AiServiceOrderRequest(String userName, String userEmail, String agentIdentifier, String serviceType, String requirements) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.agentIdentifier = agentIdentifier;
        this.serviceType = serviceType;
        this.requirements = requirements;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getAgentIdentifier() {
        return agentIdentifier;
    }

    public void setAgentIdentifier(String agentIdentifier) {
        this.agentIdentifier = agentIdentifier;
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
