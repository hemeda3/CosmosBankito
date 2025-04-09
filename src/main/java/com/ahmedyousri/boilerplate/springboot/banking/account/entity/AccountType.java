package com.ahmedyousri.boilerplate.springboot.banking.account.entity;

/**
 * Enum representing the type of an account.
 */
public enum AccountType {
    CHECKING_ACCOUNT("currentAccount"),
    SAVINGS_ACCOUNT("savingsAccount"),
    LOAN_ACCOUNT("loanAccount"),
    CREDIT_CARD_ACCOUNT("creditCard"),
    WALLET_ACCOUNT("walletAccount"),
    SYSTEM_ACCOUNT("system_account");  // Added for system accounts
    
    private final String value;
    
    AccountType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Convert a string value to an AccountType enum.
     * 
     * @param value The string value
     * @return The AccountType enum
     */
    public static AccountType fromValue(String value) {
        for (AccountType type : AccountType.values()) {
            if (type.value.equals(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid account type: " + value);
    }
}
