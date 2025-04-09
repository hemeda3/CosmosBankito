package com.ahmedyousri.boilerplate.springboot.banking.blockchain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a transaction record on the blockchain.
 * This class contains information about a blockchain transaction.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecord {
    
    /**
     * The hash of the transaction.
     */
    private String hash;
    
    /**
     * The transaction hash (alias for hash).
     */
    private String transactionHash;
    
    /**
     * The address of the sender.
     */
    private String from;
    
    /**
     * The account ID of the sender.
     */
    private UUID fromAccount;
    
    /**
     * The account ID of the recipient.
     */
    private UUID toAccount;
    
    /**
     * The address of the recipient.
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
     * The nonce of the transaction.
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
     * The hash of the block containing the transaction.
     */
    private String blockHash;
    
    /**
     * The number of the block containing the transaction.
     */
    private long blockNumber;
    
    /**
     * The transaction index within the block.
     */
    private int transactionIndex;
    
    /**
     * The status of the transaction.
     */
    private String status;
    
    /**
     * The type of the transaction.
     */
    private String type;
    
    /**
     * The input data of the transaction.
     */
    private String input;
    
    /**
     * The description or memo of the transaction.
     */
    private String description;
    
    /**
     * The reference ID of the transaction.
     */
    private String referenceId;
}
