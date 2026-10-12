package com.dmitryefremov.site.agent.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "agent_orders")
public class AgentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_type", nullable = false, length = 100)
    private String serviceType;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "contact_email", nullable = false)
    private String contactEmail;

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "agent_name")
    private String agentName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public AgentOrder() {
    }

    public AgentOrder(String serviceType, String clientName, String contactEmail, String requirements, String agentName) {
        this.serviceType = serviceType;
        this.clientName = clientName;
        this.contactEmail = contactEmail;
        this.requirements = requirements;
        this.agentName = agentName;
    }

    public Long getId() {
        return id;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentOrder that = (AgentOrder) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
