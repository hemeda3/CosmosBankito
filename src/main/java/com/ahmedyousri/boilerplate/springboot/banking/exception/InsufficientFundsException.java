package com.ahmedyousri.boilerplate.springboot.banking.exception;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Exception thrown when an account has insufficient funds for a transaction.
 * This exception provides detailed information about the account, requested amount, and available balance.
 */
public class InsufficientFundsException extends BusinessRuleException {
    
    private final UUID accountId;
    private final BigDecimal requestedAmount;
    private final BigDecimal availableBalance;
    
    /**
     * Constructs a new InsufficientFundsException with the specified account ID, requested amount, and available balance.
     *
     * @param accountId        The ID of the account with insufficient funds
     * @param requestedAmount  The amount that was requested
     * @param availableBalance The available balance in the account
     */
    public InsufficientFundsException(UUID accountId, BigDecimal requestedAmount, BigDecimal availableBalance) {
        super("Insufficient funds in account " + accountId + ": requested " +
                requestedAmount + " but available balance is " + availableBalance,
                "INSUFFICIENT_FUNDS");
        this.accountId = accountId;
        this.requestedAmount = requestedAmount;
        this.availableBalance = availableBalance;
    }
    
    /**
     * Get the ID of the account with insufficient funds.
     *
     * @return The account ID
     */
    public UUID getAccountId() {
        return accountId;
    }
    
    /**
     * Get the amount that was requested.
     *
     * @return The requested amount
     */
    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }
    
    /**
     * Get the available balance in the account.
     *
     * @return The available balance
     */
    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }
    
    /**
     * Get the shortfall amount (the difference between the requested amount and the available balance).
     *
     * @return The shortfall amount
     */
    public BigDecimal getShortfall() {
        return requestedAmount.subtract(availableBalance);
    }
}
