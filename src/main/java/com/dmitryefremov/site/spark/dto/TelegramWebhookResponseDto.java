package com.dmitryefremov.site.spark.dto;

public class TelegramWebhookResponseDto {

    private boolean success;
    private String message;
    private Long sparkId;

    public TelegramWebhookResponseDto() {
    }

    public TelegramWebhookResponseDto(boolean success, String message, Long sparkId) {
        this.success = success;
        this.message = message;
        this.sparkId = sparkId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getSparkId() {
        return sparkId;
    }

    public void setSparkId(Long sparkId) {
        this.sparkId = sparkId;
    }
}
