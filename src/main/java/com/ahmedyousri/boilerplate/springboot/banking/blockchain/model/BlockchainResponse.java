package com.ahmedyousri.boilerplate.springboot.banking.blockchain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response from a blockchain operation.
 * This class represents the result of executing a command on the blockchain.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockchainResponse {
    
    /**
     * Whether the operation was successful.
     */
    private boolean successful;
    
    /**
     * The error code if the operation failed.
     */
    private String errorCode;
    
    /**
     * The error message if the operation failed.
     */
    private String errorMessage;
    
    /**
     * The hash of the transaction.
     */
    private String transactionHash;
    
    /**
     * The hash of the block containing the transaction.
     */
    private String blockHash;
    
    /**
     * The number of the block containing the transaction.
     */
    private long blockNumber;
    
    /**
     * The gas used by the transaction.
     */
    private long gasUsed;
    
    /**
     * The timestamp of the response.
     */
    private LocalDateTime timestamp;
    
    /**
     * Additional data associated with the response.
     */
    private String data;
    
    /**
     * Check if the operation was successful.
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return successful;
    }
    
    /**
     * Get the error message if the operation failed.
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Get the error code if the operation failed.
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Get the hash of the transaction.
     * @return the transaction hash
     */
    public String getTransactionHash() {
        return transactionHash;
    }
}
