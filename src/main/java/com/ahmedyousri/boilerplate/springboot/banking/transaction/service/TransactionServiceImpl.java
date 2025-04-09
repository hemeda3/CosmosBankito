package com.ahmedyousri.boilerplate.springboot.banking.transaction.service;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.repository.AccountRepository;
import com.ahmedyousri.boilerplate.springboot.banking.exception.BusinessRuleException;
import com.ahmedyousri.boilerplate.springboot.banking.exception.ResourceNotFoundException;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.Transaction;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.TransactionType;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the TransactionService interface.
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);
    
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Transaction recordTransaction(Account account, TransactionType type, BigDecimal amount,
                                        String description, String referenceId) {
        // Verify we're in a transaction context
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException("No active transaction found - recordTransaction must be called within a transaction");
        }
        
        // Check for duplicate transaction
        if (transactionRepository.existsByReferenceId(referenceId)) {
            log.warn("Attempted duplicate transaction with referenceId: {}", referenceId);
            throw new BusinessRuleException("Duplicate transaction detected", "DUPLICATE_TRANSACTION");
        }
        
        Transaction transaction = Transaction.builder()
                .account(account)
                .type(type)
                .amount(amount)
                .currencyCode(account.getCurrencyCode())
                .balanceAfterTransaction(account.getCurrentBalance())
                .timestamp(LocalDateTime.now())
                .description(description)
                .referenceId(referenceId)
                .build();
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        log.info("Recorded {} transaction of {} {} for account {}", 
                type, amount, account.getCurrencyCode(), account.getId());
        
        return savedTransaction;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getAccountTransactions(UUID accountId, Pageable pageable) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        
        return transactionRepository.findByAccountOrderByTimestampDesc(account, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getAccountTransactionsByType(UUID accountId, TransactionType type, Pageable pageable) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        
        return transactionRepository.findByAccountAndTypeOrderByTimestampDesc(account, type, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getAccountTransactionsByDateRange(UUID accountId, 
                                                             LocalDate startDate, 
                                                             LocalDate endDate) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        return transactionRepository.findByAccountAndTimestampBetweenOrderByTimestampDesc(
                account, startDateTime, endDateTime);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByReferenceId(UUID referenceId) {
        return transactionRepository.findByReferenceIdOrderByTimestampDesc(referenceId.toString());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Transaction getTransactionDetails(UUID accountId, UUID transactionId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        
        Transaction transaction = transactionRepository.findByAccountAndId(account, transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));
        
        return transaction;
    }
}
