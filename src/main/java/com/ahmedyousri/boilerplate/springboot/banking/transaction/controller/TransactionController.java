package com.ahmedyousri.boilerplate.springboot.banking.transaction.controller;

import com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.Transaction;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.TransactionType;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.mapper.TransactionMapper;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.service.TransactionService;
import com.ahmedyousri.boilerplate.springboot.model.generated.TransactionDetailResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.TransactionsListResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for transaction-related operations.
 */
@RestController
@RequestMapping("/api/v1/accounts/{accountId}/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    
    /**
     * Get paginated transaction history for an account.
     * 
     * @param accountId The account ID
     * @param page Page number (1-based)
     * @param size Page size
     * @param type Optional transaction type filter (DEBIT or CREDIT)
     * @return Page of transactions
     */
    @GetMapping
    public ResponseEntity<TransactionsListResponse> getAccountTransactions(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type) {
        
        // Convert to 0-based page index
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("timestamp").descending());
        
        Page<Transaction> transactions;
        if (type != null) {
            TransactionType transactionType = TransactionType.fromValue(type.toLowerCase());
            transactions = transactionService.getAccountTransactionsByType(accountId, transactionType, pageable);
        } else {
            transactions = transactionService.getAccountTransactions(accountId, pageable);
        }
        
        TransactionsListResponse response = transactionMapper.toTransactionsListResponse(transactions);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get transactions for an account within a date range.
     * 
     * @param accountId The account ID
     * @param startDate Start date
     * @param endDate End date
     * @return List of transactions
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionResponse>> getAccountTransactionsByDateRange(
            @PathVariable UUID accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Transaction> transactions = transactionService.getAccountTransactionsByDateRange(
                accountId, startDate, endDate);
        
        List<TransactionResponse> response = transactionMapper.toTransactionResponseList(transactions);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get transactions by reference ID.
     * 
     * @param referenceId The reference ID
     * @return List of transactions
     */
    @GetMapping("/reference/{referenceId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByReferenceId(
            @PathVariable UUID referenceId) {
        
        List<Transaction> transactions = transactionService.getTransactionsByReferenceId(referenceId);
        
        List<TransactionResponse> response = transactionMapper.toTransactionResponseList(transactions);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get detailed information about a specific transaction.
     * 
     * @param accountId The account ID
     * @param transactionId The transaction ID
     * @return Transaction details
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDetailResponse> getTransactionDetails(
            @PathVariable UUID accountId,
            @PathVariable UUID transactionId) {
        
        Transaction transaction = transactionService.getTransactionDetails(accountId, transactionId);
        
        TransactionDetailResponse response = transactionMapper.toTransactionDetailResponse(transaction);
        
        return ResponseEntity.ok(response);
    }
}
