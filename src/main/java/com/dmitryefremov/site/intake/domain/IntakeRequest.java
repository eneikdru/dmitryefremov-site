package com.dmitryefremov.site.intake.domain;

import com.dmitryefremov.site.intake.converter.EncryptedStringConverter;
import jakarta.persistence.*;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "intake_requests")
public class IntakeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "client_email", nullable = false)
    private String clientEmail;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "audit_payload", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = EncryptedStringConverter.class)
    private String auditPayload;

    @Column(name = "status", nullable = false)
    private String status = "NEW";

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public IntakeRequest() {
    }

    public IntakeRequest(String clientName, String clientEmail, String companyName, String auditPayload) {
        this(clientName, clientEmail, companyName, auditPayload, Clock.systemUTC());
    }

    public IntakeRequest(String clientName, String clientEmail, String companyName, String auditPayload, Clock clock) {
        this(clientName, clientEmail, companyName, auditPayload, clock != null ? OffsetDateTime.now(clock) : OffsetDateTime.now());
    }

    public IntakeRequest(String clientName, String clientEmail, String companyName, String auditPayload, OffsetDateTime timestamp) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.companyName = companyName;
        this.auditPayload = auditPayload;
        this.status = "NEW";
        this.createdAt = timestamp;
        this.updatedAt = timestamp;
    }

    public Long getId() {
        return id;
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

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntakeRequest that = (IntakeRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
