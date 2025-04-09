package com.ahmedyousri.boilerplate.springboot.banking.transaction.repository;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.Transaction;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Transaction entities.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    
    /**
     * Find all transactions for an account with pagination.
     * 
     * @param account The account
     * @param pageable Pagination information
     * @return Page of transactions
     */
    Page<Transaction> findByAccount(Account account, Pageable pageable);
    
    /**
     * Find all transactions for an account ordered by timestamp descending.
     * 
     * @param account The account
     * @param pageable Pagination information
     * @return Page of transactions
     */
    Page<Transaction> findByAccountOrderByTimestampDesc(Account account, Pageable pageable);
    
    /**
     * Find all transactions for an account with a specific type.
     * 
     * @param account The account
     * @param type The transaction type (DEBIT or CREDIT)
     * @param pageable Pagination information
     * @return Page of transactions
     */
    Page<Transaction> findByAccountAndType(Account account, TransactionType type, Pageable pageable);
    
    /**
     * Find all transactions for an account with a specific type ordered by timestamp descending.
     * 
     * @param account The account
     * @param type The transaction type (DEBIT or CREDIT)
     * @param pageable Pagination information
     * @return Page of transactions
     */
    Page<Transaction> findByAccountAndTypeOrderByTimestampDesc(Account account, TransactionType type, Pageable pageable);
    
    /**
     * Find all transactions for an account within a date range.
     * 
     * @param account The account
     * @param startDate The start date
     * @param endDate The end date
     * @return List of transactions
     */
    List<Transaction> findByAccountAndTimestampBetweenOrderByTimestampDesc(
            Account account, LocalDateTime startDate, LocalDateTime endDate);
    
    List<Transaction> findByAccountAndTimestampBetween(
            Account account, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find all transactions with a specific reference ID.
     * 
     * @param referenceId The reference ID
     * @return List of transactions
     */
    List<Transaction> findByReferenceId(String referenceId);
    
    /**
     * Find all transactions with a specific reference ID ordered by timestamp descending.
     * 
     * @param referenceId The reference ID
     * @return List of transactions
     */
    List<Transaction> findByReferenceIdOrderByTimestampDesc(String referenceId);
    
    /**
     * Find a specific transaction for an account.
     * 
     * @param account The account
     * @param id The transaction ID
     * @return Optional transaction
     */
    Optional<Transaction> findByAccountAndId(Account account, UUID id);
    
    /**
     * Check if a transaction with the given reference ID exists.
     * 
     * @param referenceId The reference ID
     * @return True if a transaction with the reference ID exists, false otherwise
     */
    boolean existsByReferenceId(String referenceId);
    
    /**
     * Check if a transaction with the given account, reference ID, and type exists.
     * 
     * @param account The account
     * @param referenceId The reference ID
     * @param type The transaction type
     * @return True if a transaction with the account, reference ID, and type exists, false otherwise
     */
    boolean existsByAccountAndReferenceIdAndType(Account account, String referenceId, TransactionType type);
}
