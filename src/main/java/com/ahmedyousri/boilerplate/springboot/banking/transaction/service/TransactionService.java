package com.ahmedyousri.boilerplate.springboot.banking.transaction.service;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.Transaction;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing transactions.
 */
public interface TransactionService {
    
    /**
     * Record a transaction for an account.
     * 
     * @param account The account
     * @param type The transaction type (DEBIT or CREDIT)
     * @param amount The transaction amount
     * @param description The transaction description
     * @param referenceId The reference ID linking to the source operation
     * @return The created transaction
     */
    Transaction recordTransaction(Account account, TransactionType type, BigDecimal amount,
                                 String description, String referenceId);
    
    /**
     * Get paginated transaction history for an account.
     * 
     * @param accountId The account ID
     * @param pageable Pagination information
     * @return Page of transactions
     */
    Page<Transaction> getAccountTransactions(UUID accountId, Pageable pageable);
    
    /**
     * Get paginated transaction history for an account filtered by type.
     * 
     * @param accountId The account ID
     * @param type The transaction type (DEBIT or CREDIT)
     * @param pageable Pagination information
     * @return Page of transactions
     */
    Page<Transaction> getAccountTransactionsByType(UUID accountId, TransactionType type, Pageable pageable);
    
    /**
     * Get transactions for an account within a date range.
     * 
     * @param accountId The account ID
     * @param startDate The start date
     * @param endDate The end date
     * @return List of transactions
     */
    List<Transaction> getAccountTransactionsByDateRange(UUID accountId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get transactions by reference ID.
     * 
     * @param referenceId The reference ID
     * @return List of transactions
     */
    List<Transaction> getTransactionsByReferenceId(UUID referenceId);
    
    /**
     * Get detailed information about a specific transaction.
     * 
     * @param accountId The account ID
     * @param transactionId The transaction ID
     * @return The transaction
     */
    Transaction getTransactionDetails(UUID accountId, UUID transactionId);
}
