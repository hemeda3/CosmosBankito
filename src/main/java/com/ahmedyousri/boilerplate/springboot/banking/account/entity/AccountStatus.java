package com.ahmedyousri.boilerplate.springboot.banking.account.entity;

public enum AccountStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    DORMANT("dormant"),
    FROZEN("frozen"),
    CLOSED("closed"),
    PENDING("pending"),
    BLOCKED("blocked");

    private final String value;

    AccountStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AccountStatus fromValue(String value) {
        for (AccountStatus status : AccountStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unexpected value: " + value);
    }
}
