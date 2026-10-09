package com.dmitryefremov.site.podcast.dto;

import java.time.OffsetDateTime;

public class ErrorResponseDto {
    private int status;
    private String message;
    private OffsetDateTime timestamp;

    public ErrorResponseDto() {
    }

    public ErrorResponseDto(int status, String message, OffsetDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
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
