package com.dmitryefremov.site.agent.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "agent_interaction_telemetry")
public class AgentInteractionTelemetry {

    @Id
    @Column(name = "interaction_type", nullable = false)
    private String interactionType;

    @Column(name = "counter", nullable = false)
    private long counter;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public AgentInteractionTelemetry() {
    }

    public AgentInteractionTelemetry(String interactionType, long counter, OffsetDateTime updatedAt) {
        this.interactionType = interactionType;
        this.counter = counter;
        this.updatedAt = updatedAt;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(String interactionType) {
        this.interactionType = interactionType;
    }

    public long getCounter() {
        return counter;
    }

    public void setCounter(long counter) {
        this.counter = counter;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
