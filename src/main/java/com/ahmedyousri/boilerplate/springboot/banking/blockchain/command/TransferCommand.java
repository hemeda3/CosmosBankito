package com.ahmedyousri.boilerplate.springboot.banking.blockchain.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Command for transferring funds between accounts on the blockchain.
 * This command represents a transfer operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferCommand implements FinancialCommand {
    
    /**
     * The ID of the account to transfer funds from.
     */
    private UUID fromAccountId;
    
    /**
     * The ID of the account to transfer funds to.
     */
    private UUID toAccountId;
    
    /**
     * The amount to transfer.
     */
    private BigDecimal amount;
    
    /**
     * The description of the transfer.
     */
    private String description;
    
    /**
     * The currency code of the transfer.
     */
    private String currencyCode;
    
    /**
     * The reference ID of the transfer (if applicable).
     */
    private UUID referenceId;
    
    @Override
    public CommandType getType() {
        return CommandType.TRANSFER;
    }
}
