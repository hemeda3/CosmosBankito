package com.ahmedyousri.boilerplate.springboot.banking.blockchain.mock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a block in the mock blockchain.
 * This class is used to simulate blockchain blocks for testing and development.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockBlock {
    
    /**
     * The number of the block.
     */
    private long number;
    
    /**
     * The hash of the block (hex string).
     */
    private String hash;
    
    /**
     * The hash of the parent block (hex string).
     */
    private String parentHash;
    
    /**
     * The timestamp when the block was created.
     */
    private LocalDateTime timestamp;
    
    /**
     * The miner of the block (hex string).
     */
    private String miner;
    
    /**
     * The difficulty of the block.
     */
    private long difficulty;
    
    /**
     * The total difficulty of the chain up to this block.
     */
    private long totalDifficulty;
    
    /**
     * The size of the block in bytes.
     */
    private long size;
    
    /**
     * The gas limit of the block.
     */
    private long gasLimit;
    
    /**
     * The gas used by the block.
     */
    private long gasUsed;
    
    /**
     * The nonce of the block.
     */
    private String nonce;
    
    /**
     * The extra data of the block (hex string).
     */
    private String extraData;
    
    /**
     * The list of transaction hashes in the block.
     */
    @Builder.Default
    private List<String> transactionHashes = new ArrayList<>();
    
    /**
     * The list of transactions in the block.
     */
    @Builder.Default
    private List<MockTransaction> transactions = new ArrayList<>();
    
    /**
     * The status of the block.
     */
    @Builder.Default
    private BlockStatus status = BlockStatus.PENDING;
    
    /**
     * Add a transaction to the block.
     *
     * @param transaction The transaction to add
     */
    public void addTransaction(MockTransaction transaction) {
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        if (transactionHashes == null) {
            transactionHashes = new ArrayList<>();
        }
        
        transactions.add(transaction);
        transactionHashes.add(transaction.getHash());
        
        // Update the transaction with block information
        transaction.setBlockHash(hash);
        transaction.setBlockNumber(number);
        transaction.setTransactionIndex(transactions.size() - 1);
        transaction.setStatus(MockTransaction.TransactionStatus.CONFIRMED);
        transaction.setConfirmedAt(LocalDateTime.now());
    }
    
    /**
     * Get the number of transactions in the block.
     *
     * @return The number of transactions
     */
    public int getTransactionCount() {
        return transactions != null ? transactions.size() : 0;
    }
    
    /**
     * Mark the block as confirmed.
     */
    public void confirm() {
        this.status = BlockStatus.CONFIRMED;
        
        // Confirm all transactions in the block
        if (transactions != null) {
            for (int i = 0; i < transactions.size(); i++) {
                MockTransaction tx = transactions.get(i);
                tx.confirm(hash, number, i);
            }
        }
    }
    
    /**
     * Get the number of this block.
     * @return the number
     */
    public long getNumber() {
        return number;
    }
    
    /**
     * Get the hash of this block.
     * @return the hash
     */
    public String getHash() {
        return hash;
    }
    
    /**
     * Get the parent hash of this block.
     * @return the parent hash
     */
    public String getParentHash() {
        return parentHash;
    }
    
    /**
     * Get the timestamp of this block.
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Get the miner of this block.
     * @return the miner
     */
    public String getMiner() {
        return miner;
    }
    
    /**
     * Get the difficulty of this block.
     * @return the difficulty
     */
    public long getDifficulty() {
        return difficulty;
    }
    
    /**
     * Get the total difficulty of this block.
     * @return the total difficulty
     */
    public long getTotalDifficulty() {
        return totalDifficulty;
    }
    
    /**
     * Get the size of this block.
     * @return the size
     */
    public long getSize() {
        return size;
    }
    
    /**
     * Get the gas limit of this block.
     * @return the gas limit
     */
    public long getGasLimit() {
        return gasLimit;
    }
    
    /**
     * Get the gas used by this block.
     * @return the gas used
     */
    public long getGasUsed() {
        return gasUsed;
    }
    
    /**
     * Get the nonce of this block.
     * @return the nonce
     */
    public String getNonce() {
        return nonce;
    }
    
    /**
     * Get the extra data of this block.
     * @return the extra data
     */
    public String getExtraData() {
        return extraData;
    }
    
    /**
     * Get the transaction hashes in this block.
     * @return the transaction hashes
     */
    public List<String> getTransactionHashes() {
        return transactionHashes;
    }
    
    /**
     * Get the status of this block.
     * @return the status
     */
    public BlockStatus getStatus() {
        return status;
    }
    
    /**
     * Set the gas used by this block.
     * @param gasUsed the gas used
     */
    public void setGasUsed(long gasUsed) {
        this.gasUsed = gasUsed;
    }
    
    /**
     * The status of a blockchain block.
     */
    public enum BlockStatus {
        /**
         * The block is pending confirmation.
         */
        PENDING,
        
        /**
         * The block has been confirmed.
         */
        CONFIRMED,
        
        /**
         * The block has been orphaned.
         */
        ORPHANED
    }
}
