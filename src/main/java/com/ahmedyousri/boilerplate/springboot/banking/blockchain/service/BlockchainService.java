package com.ahmedyousri.boilerplate.springboot.banking.blockchain.service;

import com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.FinancialCommand;
import com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.AccountBalance;
import com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.BlockchainResponse;
import com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.TransactionRecord;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for interacting with the blockchain.
 * This interface defines the operations that can be performed on the blockchain.
 */
public interface BlockchainService {
    
    /**
     * Execute a financial command on the blockchain.
     *
     * @param command The command to execute
     * @return The response from the blockchain
     */
    BlockchainResponse executeCommand(FinancialCommand command);
    
    /**
     * Execute a financial command on the blockchain asynchronously.
     *
     * @param command The command to execute
     * @return A future that will complete with the response from the blockchain
     */
    CompletableFuture<BlockchainResponse> executeCommandAsync(FinancialCommand command);
    
    /**
     * Get the balance of an account on the blockchain.
     *
     * @param accountId The ID of the account
     * @return The account balance
     */
    AccountBalance getAccountBalance(UUID accountId);
    
    /**
     * Get the balance of an account on the blockchain asynchronously.
     *
     * @param accountId The ID of the account
     * @return A future that will complete with the account balance
     */
    CompletableFuture<AccountBalance> getAccountBalanceAsync(UUID accountId);
    
    /**
     * Get the transaction history of an account on the blockchain.
     *
     * @param accountId The ID of the account
     * @param limit     The maximum number of transactions to return
     * @return The transaction history
     */
    List<TransactionRecord> getTransactionHistory(UUID accountId, int limit);
    
    /**
     * Get the transaction history of an account on the blockchain asynchronously.
     *
     * @param accountId The ID of the account
     * @param limit     The maximum number of transactions to return
     * @return A future that will complete with the transaction history
     */
    CompletableFuture<List<TransactionRecord>> getTransactionHistoryAsync(UUID accountId, int limit);
    
    /**
     * Get a transaction on the blockchain.
     *
     * @param transactionHash The hash of the transaction
     * @return The transaction, or null if not found
     */
    TransactionRecord getTransaction(String transactionHash);
    
    /**
     * Get a transaction on the blockchain asynchronously.
     *
     * @param transactionHash The hash of the transaction
     * @return A future that will complete with the transaction, or null if not found
     */
    CompletableFuture<TransactionRecord> getTransactionAsync(String transactionHash);
}
