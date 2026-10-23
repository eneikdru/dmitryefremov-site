package com.dmitryefremov.site.intake.dto;

import java.time.OffsetDateTime;

public class ClientIntakeResponseDto {

    private Long id;
    private String clientType;
    private String clientName;
    private String clientEmail;
    private String companyName;
    private String wishlistPayload;
    private String status;
    private AgentMetadataDto agentMetadata;
    private String source;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public ClientIntakeResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
