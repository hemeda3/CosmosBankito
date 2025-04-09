package com.ahmedyousri.boilerplate.springboot.banking.account.mapper;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountStatus;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountType;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Statement;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountCreationRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountDetailResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountStatementsResponseStatementsInner;
import com.ahmedyousri.boilerplate.springboot.model.generated.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountMapper {
    
    /**
     * Convert Account entity to AccountResponse DTO
     */
    public AccountResponse toAccountResponse(Account account) {
        if (account == null) {
            return null;
        }
        
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setIban(account.getIban());
        response.setAccountName(account.getAccountName());
        response.setType(AccountResponse.TypeEnum.fromValue(account.getType().getValue()));
        response.setCurrencyCode(account.getCurrencyCode());
        response.setStatus(AccountResponse.StatusEnum.fromValue(account.getStatus().getValue()));
        
        Money currentBalance = new Money();
        currentBalance.setAmount(account.getCurrentBalance().toString());
        currentBalance.setCurrencyCode(account.getCurrencyCode());
        response.setCurrentBalance(currentBalance);
        
        Money availableBalance = new Money();
        availableBalance.setAmount(account.getAvailableBalance().toString());
        availableBalance.setCurrencyCode(account.getCurrencyCode());
        response.setAvailableBalance(availableBalance);
        
        response.setOpenedDate(toOffsetDateTime(account.getOpenedDate()));
        response.setLastTransactionDate(account.getLastTransactionDate() != null ? 
                toOffsetDateTime(account.getLastTransactionDate()) : null);
        response.setInterestRate(account.getInterestRate());
        response.setCreatedAt(toOffsetDateTime(account.getCreatedAt()));
        
        return response;
    }
    
    /**
     * Convert Account entity to AccountDetailResponse DTO
     */
    public AccountDetailResponse toAccountDetailResponse(Account account) {
        if (account == null) {
            return null;
        }
        
        AccountDetailResponse response = new AccountDetailResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setIban(account.getIban());
        response.setAccountName(account.getAccountName());
        response.setType(AccountDetailResponse.TypeEnum.fromValue(account.getType().getValue()));
        response.setCurrencyCode(account.getCurrencyCode());
        response.setStatus(AccountDetailResponse.StatusEnum.fromValue(account.getStatus().getValue()));
        
        Money currentBalance = new Money();
        currentBalance.setAmount(account.getCurrentBalance().toString());
        currentBalance.setCurrencyCode(account.getCurrencyCode());
        response.setCurrentBalance(currentBalance);
        
        Money availableBalance = new Money();
        availableBalance.setAmount(account.getAvailableBalance().toString());
        availableBalance.setCurrencyCode(account.getCurrencyCode());
        response.setAvailableBalance(availableBalance);
        
        response.setOpenedDate(toOffsetDateTime(account.getOpenedDate()));
        response.setLastTransactionDate(account.getLastTransactionDate() != null ? 
                toOffsetDateTime(account.getLastTransactionDate()) : null);
        response.setInterestRate(account.getInterestRate());
        response.setCreatedAt(toOffsetDateTime(account.getCreatedAt()));
        
        // Additional fields for detailed response can be added here
        
        return response;
    }
    
    /**
     * Convert AccountCreationRequest DTO to Account entity
     */
    public Account toAccount(AccountCreationRequest request) {
        if (request == null) {
            return null;
        }
        
        Account account = new Account();
        account.setAccountName(request.getAccountName());
        account.setType(AccountType.fromValue(request.getType().getValue()));
        account.setCurrencyCode(request.getCurrencyCode());
        account.setStatus(AccountStatus.PENDING); // New accounts start as pending
        account.setCurrentBalance(BigDecimal.ZERO);
        account.setAvailableBalance(BigDecimal.ZERO);
        account.setOpenedDate(LocalDateTime.now());
        
        return account;
    }
    
    /**
     * Convert Statement entity to AccountStatementsResponseStatementsInner DTO
     */
    public AccountStatementsResponseStatementsInner toStatementResponse(Statement statement) {
        if (statement == null) {
            return null;
        }
        
        AccountStatementsResponseStatementsInner response = new AccountStatementsResponseStatementsInner();
        response.setId(statement.getId());
        response.setPeriodStart(LocalDate.parse(statement.getPeriod().split(" - ")[0]));
        response.setPeriodEnd(LocalDate.parse(statement.getPeriod().split(" - ")[1]));
        response.setDownloadUrl(java.net.URI.create("/accounts/" + statement.getAccount().getId() + "/statement/" + statement.getId()));
        response.setGeneratedDate(toOffsetDateTime(statement.getGeneratedAt()));
        response.setFileSize(1024); // Placeholder file size
        
        return response;
    }
    
    /**
     * Convert a list of Statement entities to a list of AccountStatementsResponseStatementsInner DTOs
     */
    public List<AccountStatementsResponseStatementsInner> toStatementResponseList(List<Statement> statements) {
        if (statements == null) {
            return null;
        }
        
        return statements.stream()
                .map(this::toStatementResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert LocalDateTime to OffsetDateTime
     */
    private OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }
}
