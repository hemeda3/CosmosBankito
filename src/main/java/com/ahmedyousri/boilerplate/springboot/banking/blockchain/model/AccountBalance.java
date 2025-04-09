package com.ahmedyousri.boilerplate.springboot.banking.blockchain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the balance of an account on the blockchain.
 * This class contains information about an account's balance and related metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalance {
    
    /**
     * The ID of the account.
     */
    private UUID accountId;
    
    /**
     * The balance of the account.
     */
    private BigDecimal balance;
    
    /**
     * The currency code of the account.
     */
    private String currencyCode;
    
    /**
     * The blockchain address of the account.
     */
    private String address;
    
    /**
     * The block number at which the balance was retrieved.
     */
    private long blockNumber;
    
    /**
     * The timestamp when the balance was retrieved.
     */
    private LocalDateTime timestamp;
    
    /**
     * The error message if there was an error retrieving the balance.
     */
    private String error;
    
    /**
     * Whether the account exists on the blockchain.
     */
    @Builder.Default
    private boolean exists = true;
    
    /**
     * Get the amount of the balance.
     * @return the balance amount
     */
    public BigDecimal getAmount() {
        return balance;
    }
}
