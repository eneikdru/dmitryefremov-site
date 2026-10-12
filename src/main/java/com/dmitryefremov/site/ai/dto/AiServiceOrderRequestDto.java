package com.dmitryefremov.site.ai.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AiServiceOrderRequestDto {

    @NotBlank(message = "userName must not be blank")
    private String userName;

    @NotBlank(message = "userEmail must not be blank")
    @Email(message = "userEmail must be a valid email address")
    private String userEmail;

    private String agentIdentifier;

    @NotBlank(message = "serviceType must not be blank")
    private String serviceType;

    private String requirements;

    public AiServiceOrderRequestDto() {
    }

    public AiServiceOrderRequestDto(String userName, String userEmail, String agentIdentifier, String serviceType, String requirements) {
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
