package com.ahmedyousri.boilerplate.springboot.banking.blockchain.mock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a transaction in the mock blockchain.
 * This class is used to simulate blockchain transactions for testing and development.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockTransaction {
    
    /**
     * The unique identifier of the transaction.
     */
    private UUID id;
    
    /**
     * The hash of the transaction (hex string).
     */
    private String hash;
    
    /**
     * The address of the sender (hex string).
     */
    private String from;
    
    /**
     * The address of the recipient (hex string).
     */
    private String to;
    
    /**
     * The amount of the transaction.
     */
    private BigDecimal amount;
    
    /**
     * The currency code of the transaction.
     */
    private String currencyCode;
    
    /**
     * The nonce of the transaction (used for ordering).
     */
    private long nonce;
    
    /**
     * The gas price of the transaction.
     */
    private long gasPrice;
    
    /**
     * The gas limit of the transaction.
     */
    private long gasLimit;
    
    /**
     * The gas used by the transaction.
     */
    private long gasUsed;
    
    /**
     * The timestamp when the transaction was created.
     */
    private LocalDateTime createdAt;
    
    /**
     * The timestamp when the transaction was confirmed.
     */
    private LocalDateTime confirmedAt;
    
    /**
     * The block hash of the block containing the transaction.
     */
    private String blockHash;
    
    /**
     * The block number of the block containing the transaction.
     */
    private long blockNumber;
    
    /**
     * The transaction index within the block.
     */
    private int transactionIndex;
    
    /**
     * The status of the transaction.
     */
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;
    
    /**
     * The type of the transaction.
     */
    @Builder.Default
    private TransactionType type = TransactionType.TRANSFER;
    
    /**
     * The input data of the transaction (hex string).
     */
    private String input;
    
    /**
     * The description or memo of the transaction.
     */
    private String description;
    
    /**
     * The reference ID of the transaction (if applicable).
     */
    private String referenceId;
    
    /**
     * Additional metadata for the transaction.
     */
    @Builder.Default
    private Map<String, String> metadata = new HashMap<>();
    
    /**
     * The error message if the transaction failed.
     */
    private String errorMessage;
    
    /**
     * Add metadata to the transaction.
     *
     * @param key   The metadata key
     * @param value The metadata value
     */
    public void addMetadata(String key, String value) {
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        metadata.put(key, value);
    }
    
    /**
     * Mark the transaction as confirmed.
     *
     * @param blockHash       The hash of the block containing the transaction
     * @param blockNumber     The number of the block containing the transaction
     * @param transactionIndex The index of the transaction within the block
     */
    public void confirm(String blockHash, long blockNumber, int transactionIndex) {
        this.blockHash = blockHash;
        this.blockNumber = blockNumber;
        this.transactionIndex = transactionIndex;
        this.confirmedAt = LocalDateTime.now();
        this.status = TransactionStatus.CONFIRMED;
    }
    
    /**
     * Mark the transaction as failed.
     *
     * @param errorMessage The error message
     */
    public void fail(String errorMessage) {
        this.errorMessage = errorMessage;
        this.status = TransactionStatus.FAILED;
    }
    
    /**
     * Get the hash of this transaction.
     * @return the hash
     */
    public String getHash() {
        return hash;
    }
    
    /**
     * Get the sender address of this transaction.
     * @return the sender address
     */
    public String getFrom() {
        return from;
    }
    
    /**
     * Get the recipient address of this transaction.
     * @return the recipient address
     */
    public String getTo() {
        return to;
    }
    
    /**
     * Get the amount of this transaction.
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }
    
    /**
     * Get the currency code of this transaction.
     * @return the currency code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    /**
     * Get the status of this transaction.
     * @return the status
     */
    public TransactionStatus getStatus() {
        return status;
    }
    
    /**
     * Get the creation timestamp of this transaction.
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Get the confirmation timestamp of this transaction.
     * @return the confirmation timestamp
     */
    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }
    
    /**
     * Get the block hash of this transaction.
     * @return the block hash
     */
    public String getBlockHash() {
        return blockHash;
    }
    
    /**
     * Get the block number of this transaction.
     * @return the block number
     */
    public long getBlockNumber() {
        return blockNumber;
    }
    
    /**
     * Get the description of this transaction.
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get the nonce of this transaction.
     * @return the nonce
     */
    public long getNonce() {
        return nonce;
    }
    
    /**
     * Get the gas price of this transaction.
     * @return the gas price
     */
    public long getGasPrice() {
        return gasPrice;
    }
    
    /**
     * Get the gas limit of this transaction.
     * @return the gas limit
     */
    public long getGasLimit() {
        return gasLimit;
    }
    
    /**
     * Get the gas used by this transaction.
     * @return the gas used
     */
    public long getGasUsed() {
        return gasUsed;
    }
    
    /**
     * Get the transaction index of this transaction.
     * @return the transaction index
     */
    public int getTransactionIndex() {
        return transactionIndex;
    }
    
    /**
     * Get the type of this transaction.
     * @return the type
     */
    public TransactionType getType() {
        return type;
    }
    
    /**
     * Get the input data of this transaction.
     * @return the input data
     */
    public String getInput() {
        return input;
    }
    
    /**
     * Get the reference ID of this transaction.
     * @return the reference ID
     */
    public String getReferenceId() {
        return referenceId;
    }
    
    /**
     * Set the status of this transaction.
     * @param status the status
     */
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
    
    /**
     * Set the block hash of this transaction.
     * @param blockHash the block hash
     */
    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }
    
    /**
     * Set the block number of this transaction.
     * @param blockNumber the block number
     */
    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }
    
    /**
     * Set the transaction index of this transaction.
     * @param transactionIndex the transaction index
     */
    public void setTransactionIndex(int transactionIndex) {
        this.transactionIndex = transactionIndex;
    }
    
    /**
     * Set the confirmation timestamp of this transaction.
     * @param confirmedAt the confirmation timestamp
     */
    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
    
    /**
     * The status of a blockchain transaction.
     */
    public enum TransactionStatus {
        /**
         * The transaction is pending confirmation.
         */
        PENDING,
        
        /**
         * The transaction has been confirmed.
         */
        CONFIRMED,
        
        /**
         * The transaction has failed.
         */
        FAILED,
        
        /**
         * The transaction has been rejected.
         */
        REJECTED
    }
    
    /**
     * The type of a blockchain transaction.
     */
    public enum TransactionType {
        /**
         * A transfer of funds between accounts.
         */
        TRANSFER,
        
        /**
         * A contract deployment.
         */
        CONTRACT_DEPLOYMENT,
        
        /**
         * A contract function call.
         */
        CONTRACT_CALL,
        
        /**
         * A token transfer.
         */
        TOKEN_TRANSFER,
        
        /**
         * A system transaction.
         */
        SYSTEM
    }
}
