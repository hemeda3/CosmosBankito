package com.ahmedyousri.boilerplate.springboot.banking.transaction.entity;

/**
 * Enum representing the type of a transaction.
 */
public enum TransactionType {
    CREDIT("credit"),
    DEBIT("debit"),
    TRANSFER("transfer"),
    FEE("fee"),
    INTEREST("interest"),
    COMPENSATION("compensation");  // Added for compensation transactions
    
    private final String value;
    
    TransactionType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Convert a string value to a TransactionType enum.
     * 
     * @param value The string value
     * @return The TransactionType enum
     */
    public static TransactionType fromValue(String value) {
        for (TransactionType type : TransactionType.values()) {
            if (type.value.equals(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid transaction type: " + value);
    }
}
