package com.dmitryefremov.site.ai.dto;

import java.time.OffsetDateTime;

public class AiServiceOrderResponseDto {

    private Long id;
    private String status;
    private String message;
    private OffsetDateTime timestamp;

    public AiServiceOrderResponseDto() {
    }

    public AiServiceOrderResponseDto(Long id, String status, String message, OffsetDateTime timestamp) {
        this.id = id;
        this.status = status;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
