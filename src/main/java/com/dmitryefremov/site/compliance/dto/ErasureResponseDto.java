package com.dmitryefremov.site.compliance.dto;

import java.time.OffsetDateTime;

public class ErasureResponseDto {

    private String userId;
    private long ordersDeleted;
    private long messagesDeleted;
    private OffsetDateTime erasedAt;
    private String statusMessage;

    public ErasureResponseDto() {
    }

    public ErasureResponseDto(String userId, long ordersDeleted, long messagesDeleted, OffsetDateTime erasedAt, String statusMessage) {
        this.userId = userId;
        this.ordersDeleted = ordersDeleted;
        this.messagesDeleted = messagesDeleted;
        this.erasedAt = erasedAt;
        this.statusMessage = statusMessage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getOrdersDeleted() {
        return ordersDeleted;
    }

    public void setOrdersDeleted(long ordersDeleted) {
        this.ordersDeleted = ordersDeleted;
    }

    public long getMessagesDeleted() {
        return messagesDeleted;
    }

    public void setMessagesDeleted(long messagesDeleted) {
        this.messagesDeleted = messagesDeleted;
    }

    public OffsetDateTime getErasedAt() {
        return erasedAt;
    }

    public void setErasedAt(OffsetDateTime erasedAt) {
        this.erasedAt = erasedAt;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
