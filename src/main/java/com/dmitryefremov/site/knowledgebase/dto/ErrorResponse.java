package com.dmitryefremov.site.knowledgebase.dto;

import java.time.OffsetDateTime;

public class ErrorResponse {
    private Integer status;
    private String message;
    private OffsetDateTime timestamp;

    public ErrorResponse() {
        this.timestamp = OffsetDateTime.now();
    }

    public ErrorResponse(Integer status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = OffsetDateTime.now();
    }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
}
