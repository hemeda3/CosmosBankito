package com.ahmedyousri.boilerplate.springboot.banking.transaction.mapper;

import com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.Transaction;
import com.ahmedyousri.boilerplate.springboot.model.generated.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Transaction entities and DTOs.
 */
@Component
public class TransactionMapper {
    
    /**
     * Convert a Transaction entity to a TransactionResponse DTO.
     * 
     * @param transaction The transaction entity
     * @return The transaction response DTO
     */
    public TransactionResponse toTransactionResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setAccountId(transaction.getAccount().getId());
        response.setType(TransactionResponse.TypeEnum.fromValue(transaction.getType().name()));
        
        Money amount = new Money();
        amount.setAmount(transaction.getAmount().toString());
        amount.setCurrencyCode(transaction.getCurrencyCode());
        response.setAmount(amount);
        
        response.setDescription(transaction.getDescription());
        response.setTimestamp(toOffsetDateTime(transaction.getTimestamp()));
        response.setReferenceId(UUID.fromString(transaction.getReferenceId()));
        
        return response;
    }
    
    /**
     * Convert a Transaction entity to a TransactionDetailResponse DTO.
     * 
     * @param transaction The transaction entity
     * @return The transaction detail response DTO
     */
    public TransactionDetailResponse toTransactionDetailResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        
        TransactionDetailResponse response = new TransactionDetailResponse();
        response.setId(transaction.getId());
        response.setAccountId(transaction.getAccount().getId());
        response.setType(TransactionDetailResponse.TypeEnum.fromValue(transaction.getType().name()));
        
        Money amount = new Money();
        amount.setAmount(transaction.getAmount().toString());
        amount.setCurrencyCode(transaction.getCurrencyCode());
        response.setAmount(amount);
        
        Money balanceAfter = new Money();
        balanceAfter.setAmount(transaction.getBalanceAfterTransaction().toString());
        balanceAfter.setCurrencyCode(transaction.getCurrencyCode());
        response.setBalanceAfterTransaction(balanceAfter);
        
        response.setDescription(transaction.getDescription());
        response.setTimestamp(toOffsetDateTime(transaction.getTimestamp()));
        response.setReferenceId(UUID.fromString(transaction.getReferenceId()));
        
        return response;
    }
    
    /**
     * Convert a list of Transaction entities to a list of TransactionResponse DTOs.
     * 
     * @param transactions The list of transaction entities
     * @return The list of transaction response DTOs
     */
    public List<TransactionResponse> toTransactionResponseList(List<Transaction> transactions) {
        if (transactions == null) {
            return null;
        }
        
        return transactions.stream()
                .map(this::toTransactionResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert a page of Transaction entities to a TransactionsListResponse DTO.
     * 
     * @param transactionsPage The page of transaction entities
     * @return The transaction list response DTO
     */
    public TransactionsListResponse toTransactionsListResponse(Page<Transaction> transactionsPage) {
        if (transactionsPage == null) {
            return null;
        }

        TransactionsListResponse response = new TransactionsListResponse();
        response.setTransactions(toTransactionResponseList(transactionsPage.getContent()));

        PaginationMetadata pagination = new PaginationMetadata();
        pagination.setTotalPages(transactionsPage.getTotalPages());
        pagination.setTotalCount((int) transactionsPage.getTotalElements());
        pagination.setCurrentPage(transactionsPage.getNumber() + 1);
        pagination.setPageSize(transactionsPage.getSize());

        response.setPagination(pagination);

        return response;
    }

    /**
     * Convert a LocalDateTime to an OffsetDateTime.
     * 
     * @param localDateTime The LocalDateTime
     * @return The OffsetDateTime
     */
    private OffsetDateTime toOffsetDateTime(java.time.LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }
}
