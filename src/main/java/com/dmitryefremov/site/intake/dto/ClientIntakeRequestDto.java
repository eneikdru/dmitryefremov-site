package com.dmitryefremov.site.intake.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClientIntakeRequestDto {

    private String clientType = "HUMAN";

    @NotBlank(message = "clientName must not be blank")
    @Size(min = 2, max = 255, message = "clientName must be between 2 and 255 characters")
    private String clientName;

    @NotBlank(message = "clientEmail must not be blank")
    @Email(message = "clientEmail must be a valid email address")
    @Size(max = 255, message = "clientEmail must not exceed 255 characters")
    private String clientEmail;

    @Size(max = 255, message = "companyName must not exceed 255 characters")
    private String companyName;

    @NotBlank(message = "wishlistPayload must not be blank")
    @Size(min = 10, max = 10000, message = "wishlistPayload must be between 10 and 10000 characters")
    private String wishlistPayload;

    private AgentMetadataDto agentMetadata;

    private String source = "WEB_PORTAL";

    public ClientIntakeRequestDto() {
    }

    public ClientIntakeRequestDto(String clientName, String clientEmail, String companyName, String wishlistPayload) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.companyName = companyName;
        this.wishlistPayload = wishlistPayload;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getWishlistPayload() {
        return wishlistPayload;
    }

    public void setWishlistPayload(String wishlistPayload) {
        this.wishlistPayload = wishlistPayload;
    }

    public AgentMetadataDto getAgentMetadata() {
        return agentMetadata;
    }

    public void setAgentMetadata(AgentMetadataDto agentMetadata) {
        this.agentMetadata = agentMetadata;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
