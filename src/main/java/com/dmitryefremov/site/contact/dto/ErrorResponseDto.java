package com.dmitryefremov.site.contact.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class ErrorResponseDto {

    private int status;
    private String message;
    private OffsetDateTime timestamp;
    private List<ValidationErrorDto> errors;

    public ErrorResponseDto() {
    }

    public ErrorResponseDto(int status, String message, OffsetDateTime timestamp) {
        this(status, message, timestamp, null);
    }

    public ErrorResponseDto(int status, String message, OffsetDateTime timestamp, List<ValidationErrorDto> errors) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.errors = errors;
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

    public List<ValidationErrorDto> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationErrorDto> errors) {
        this.errors = errors;
    }

    public static class ValidationErrorDto {
        private String field;
        private String message;

        public ValidationErrorDto() {
        }

        public ValidationErrorDto(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
