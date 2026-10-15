package com.dmitryefremov.site.intake.dto;

import java.time.OffsetDateTime;

public class IntakeResponseDto {

    private Long id;
    private String status;
    private String acknowledgementMessage;
    private OffsetDateTime timestamp;

    public IntakeResponseDto() {
    }

    public IntakeResponseDto(Long id, String status, String acknowledgementMessage, OffsetDateTime timestamp) {
        this.id = id;
        this.status = status;
        this.acknowledgementMessage = acknowledgementMessage;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAcknowledgementMessage() {
        return acknowledgementMessage;
    }

    public void setAcknowledgementMessage(String acknowledgementMessage) {
        this.acknowledgementMessage = acknowledgementMessage;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
