package com.dmitryefremov.site.knowledgebase.dto;

import java.time.Clock;
import java.time.OffsetDateTime;

public class ErrorResponse {

    private int status;
    private String message;
    private OffsetDateTime timestamp;

    public ErrorResponse() {
        this.timestamp = OffsetDateTime.now(Clock.systemUTC());
    }

    public ErrorResponse(int status, String message) {
        this(status, message, Clock.systemUTC());
    }

    public ErrorResponse(int status, String message, Clock clock) {
        this.status = status;
        this.message = message;
        this.timestamp = OffsetDateTime.now(clock != null ? clock : Clock.systemUTC());
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
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
