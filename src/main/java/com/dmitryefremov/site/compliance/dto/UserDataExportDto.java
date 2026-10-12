package com.dmitryefremov.site.compliance.dto;

import java.time.OffsetDateTime;
import java.util.List;

public class UserDataExportDto {

    private String userId;
    private List<ServiceOrderDto> orders;
    private List<DirectMessageDto> messages;
    private OffsetDateTime exportedAt;

    public UserDataExportDto() {
    }

    public UserDataExportDto(String userId, List<ServiceOrderDto> orders, List<DirectMessageDto> messages, OffsetDateTime exportedAt) {
        this.userId = userId;
        this.orders = orders;
        this.messages = messages;
        this.exportedAt = exportedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ServiceOrderDto> getOrders() {
        return orders;
    }

    public void setOrders(List<ServiceOrderDto> orders) {
        this.orders = orders;
    }

    public List<DirectMessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<DirectMessageDto> messages) {
        this.messages = messages;
    }

    public OffsetDateTime getExportedAt() {
        return exportedAt;
    }

    public void setExportedAt(OffsetDateTime exportedAt) {
        this.exportedAt = exportedAt;
    }
}
