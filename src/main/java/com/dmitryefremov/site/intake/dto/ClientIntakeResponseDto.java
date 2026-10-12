package com.dmitryefremov.site.intake.dto;

import java.time.OffsetDateTime;

public class ClientIntakeResponseDto {

    private Long id;
    private String clientName;
    private String clientEmail;
    private String companyName;
    private String status;
    private OffsetDateTime createdAt;

    public ClientIntakeResponseDto() {
    }

    public ClientIntakeResponseDto(Long id, String clientName, String clientEmail, String companyName, String status, OffsetDateTime createdAt) {
        this.id = id;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.companyName = companyName;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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
