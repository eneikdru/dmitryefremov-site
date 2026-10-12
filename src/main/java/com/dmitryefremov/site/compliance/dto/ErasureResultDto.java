package com.dmitryefremov.site.compliance.dto;

public class ErasureResultDto {
    private String userIdentifier;
    private long deletedOrdersCount;
    private long deletedMessagesCount;
    private String status;

    public ErasureResultDto() {
    }

    public ErasureResultDto(String userIdentifier, long deletedOrdersCount, long deletedMessagesCount, String status) {
        this.userIdentifier = userIdentifier;
        this.deletedOrdersCount = deletedOrdersCount;
        this.deletedMessagesCount = deletedMessagesCount;
        this.status = status;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public long getDeletedOrdersCount() {
        return deletedOrdersCount;
    }

    public void setDeletedOrdersCount(long deletedOrdersCount) {
        this.deletedOrdersCount = deletedOrdersCount;
    }

    public long getDeletedMessagesCount() {
        return deletedMessagesCount;
    }

    public void setDeletedMessagesCount(long deletedMessagesCount) {
        this.deletedMessagesCount = deletedMessagesCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
