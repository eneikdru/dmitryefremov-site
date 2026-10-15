package com.dmitryefremov.site.intake.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class IntakeSubmissionDto {

    @NotBlank(message = "Client name is required")
    private String clientName;

    @NotBlank(message = "Client email is required")
    @Email(message = "Invalid email format")
    private String clientEmail;

    private String companyName;

    @NotBlank(message = "Audit payload is required")
    private String auditPayload;

    @JsonIgnore
    private boolean isA2a;

    private String captchaToken;

    public IntakeSubmissionDto() {
    }

    public IntakeSubmissionDto(String clientName, String clientEmail, String companyName, String auditPayload) {
        this(clientName, clientEmail, companyName, auditPayload, false, null);
    }

    public IntakeSubmissionDto(String clientName, String clientEmail, String companyName, String auditPayload, boolean isA2a, String captchaToken) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.companyName = companyName;
        this.auditPayload = auditPayload;
        this.isA2a = isA2a;
        this.captchaToken = captchaToken;
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

    public String getAuditPayload() {
        return auditPayload;
    }

    public void setAuditPayload(String auditPayload) {
        this.auditPayload = auditPayload;
    }

    @JsonIgnore
    public boolean isA2a() {
        return isA2a;
    }

    @JsonIgnore
    public void setA2a(boolean a2a) {
        isA2a = a2a;
    }

    public String getCaptchaToken() {
        return captchaToken;
    }

    public void setCaptchaToken(String captchaToken) {
        this.captchaToken = captchaToken;
    }
}
