package com.dmitryefremov.site.knowledgebase.dto;

import java.time.OffsetDateTime;

public class ErrorResponseDto {
    private Integer status;
    private String message;
    private OffsetDateTime timestamp;

    public ErrorResponseDto() {}

    public ErrorResponseDto(Integer status, String message, OffsetDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
}
