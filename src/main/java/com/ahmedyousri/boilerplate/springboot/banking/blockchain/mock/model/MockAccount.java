package com.ahmedyousri.boilerplate.springboot.banking.blockchain.mock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents an account in the mock blockchain.
 * This class is used to simulate blockchain accounts for testing and development.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockAccount {
    
    /**
     * The unique identifier of the account.
     */
    private UUID id;
    
    /**
     * The blockchain address of the account (hex string).
     */
    private String address;
    
    /**
     * The balance of the account.
     */
    private BigDecimal balance;
    
    /**
     * The currency code of the account.
     */
    private String currencyCode;
    
    /**
     * The nonce of the account (used for transaction ordering).
     */
    private long nonce;
    
    /**
     * The timestamp when the account was created.
     */
    private LocalDateTime createdAt;
    
    /**
     * The timestamp when the account was last updated.
     */
    private LocalDateTime updatedAt;
    
    /**
     * The list of transaction hashes associated with this account.
     */
    @Builder.Default
    private List<String> transactionHashes = new ArrayList<>();
    
    /**
     * Whether the account is a contract account.
     */
    private boolean isContract;
    
    /**
     * The contract code (if this is a contract account).
     */
    private String contractCode;
    
    /**
     * The contract ABI (if this is a contract account).
     */
    private String contractAbi;
    
    /**
     * The owner of the account (if applicable).
     */
    private String owner;
    
    /**
     * The status of the account.
     */
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;
    
    /**
     * Increment the nonce of the account.
     *
     * @return The new nonce value
     */
    public synchronized long incrementNonce() {
        return ++nonce;
    }
    
    /**
     * Add a transaction hash to the account's transaction history.
     *
     * @param transactionHash The transaction hash to add
     */
    public synchronized void addTransactionHash(String transactionHash) {
        if (transactionHashes == null) {
            transactionHashes = new ArrayList<>();
        }
        transactionHashes.add(transactionHash);
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Credit the account with the specified amount.
     *
     * @param amount The amount to credit
     * @return The new balance
     */
    public synchronized BigDecimal credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }
        
        balance = balance.add(amount);
        updatedAt = LocalDateTime.now();
        return balance;
    }
    
    /**
     * Debit the account with the specified amount.
     *
     * @param amount The amount to debit
     * @return The new balance
     * @throws IllegalArgumentException If the amount is negative or zero
     * @throws IllegalStateException    If the account has insufficient funds
     */
    public synchronized BigDecimal debit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        
        balance = balance.subtract(amount);
        updatedAt = LocalDateTime.now();
        return balance;
    }
    
    /**
     * Get the ID of this account.
     * @return the ID
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * Get the address of this account.
     * @return the address
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Get the balance of this account.
     * @return the balance
     */
    public BigDecimal getBalance() {
        return balance;
    }
    
    /**
     * Get the currency code of this account.
     * @return the currency code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    /**
     * Get the nonce of this account.
     * @return the nonce
     */
    public long getNonce() {
        return nonce;
    }
    
    /**
     * Get the creation timestamp of this account.
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Get the last update timestamp of this account.
     * @return the last update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Get the transaction hashes associated with this account.
     * @return the transaction hashes
     */
    public List<String> getTransactionHashes() {
        return transactionHashes;
    }
    
    /**
     * Get the status of this account.
     * @return the status
     */
    public AccountStatus getStatus() {
        return status;
    }
    
    /**
     * The status of a blockchain account.
     */
    public enum AccountStatus {
        /**
         * The account is active and can participate in transactions.
         */
        ACTIVE,
        
        /**
         * The account is frozen and cannot participate in transactions.
         */
        FROZEN,
        
        /**
         * The account is closed and cannot be used.
         */
        CLOSED,
        
        /**
         * The account is pending activation.
         */
        PENDING
    }
}
