package com.dmitryefremov.site.compliance.dto;

import java.util.List;

public class UserDataExportDto {
    private String userIdentifier;
    private List<AgentOrderDto> orders;
    private List<AgentMessageDto> messages;

    public UserDataExportDto() {
    }

    public UserDataExportDto(String userIdentifier, List<AgentOrderDto> orders, List<AgentMessageDto> messages) {
        this.userIdentifier = userIdentifier;
        this.orders = orders;
        this.messages = messages;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public List<AgentOrderDto> getOrders() {
        return orders;
    }

    public void setOrders(List<AgentOrderDto> orders) {
        this.orders = orders;
    }

    public List<AgentMessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<AgentMessageDto> messages) {
        this.messages = messages;
    }
}
