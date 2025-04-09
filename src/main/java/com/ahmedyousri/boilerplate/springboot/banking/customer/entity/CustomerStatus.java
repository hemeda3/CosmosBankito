package com.ahmedyousri.boilerplate.springboot.banking.customer.entity;

public enum CustomerStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    PENDING_VERIFICATION("pendingVerification"),
    SUSPENDED("suspended"),
    CLOSED("closed");

    private final String value;

    CustomerStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CustomerStatus fromValue(String value) {
        for (CustomerStatus status : CustomerStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unexpected value: " + value);
    }
}
