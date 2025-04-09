package com.ahmedyousri.boilerplate.springboot.banking.blockchain.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command for withdrawing funds from an account on the blockchain.
 * This command represents a withdrawal operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawCommand implements FinancialCommand {
    
    /**
     * The ID of the account to withdraw funds from.
     */
    private UUID accountId;
    
    /**
     * The amount to withdraw.
     */
    private BigDecimal amount;
    
    /**
     * The description of the withdrawal.
     */
    private String description;
    
    /**
     * The reference ID of the withdrawal (if applicable).
     */
    private UUID referenceId;
    
    /**
     * Constructor for creating a withdraw command with the specified parameters.
     *
     * @param accountId   The ID of the account to withdraw funds from
     * @param amount      The amount to withdraw
     * @param description The description of the withdrawal
     */
    public WithdrawCommand(UUID accountId, BigDecimal amount, String description) {
        this.accountId = accountId;
        this.amount = amount;
        this.description = description;
    }
    
    @Override
    public CommandType getType() {
        return CommandType.WITHDRAW;
    }
}
