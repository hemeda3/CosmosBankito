package com.ahmedyousri.boilerplate.springboot.banking.blockchain.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command for depositing funds into an account on the blockchain.
 * This command represents a deposit operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositCommand implements FinancialCommand {
    
    /**
     * The ID of the account to deposit funds into.
     */
    private UUID accountId;
    
    /**
     * The amount to deposit.
     */
    private BigDecimal amount;
    
    /**
     * The description of the deposit.
     */
    private String description;
    
    /**
     * The currency code of the deposit.
     */
    private String currencyCode;
    
    /**
     * The reference ID of the deposit (if applicable).
     */
    private UUID referenceId;
    
    /**
     * Constructor for creating a deposit command with the specified parameters.
     *
     * @param accountId    The ID of the account to deposit funds into
     * @param amount       The amount to deposit
     * @param description  The description of the deposit
     * @param currencyCode The currency code of the deposit
     */
    public DepositCommand(UUID accountId, BigDecimal amount, String description, String currencyCode) {
        if (accountId == null) {
            throw new IllegalArgumentException("accountId cannot be null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (currencyCode == null || currencyCode.isEmpty()) {
            throw new IllegalArgumentException("currencyCode cannot be null or empty");
        }
        
        this.accountId = accountId;
        this.amount = amount;
        this.description = description;
        this.currencyCode = currencyCode;
    }
    
    @Override
    public CommandType getType() {
        return CommandType.DEPOSIT;
    }
}
