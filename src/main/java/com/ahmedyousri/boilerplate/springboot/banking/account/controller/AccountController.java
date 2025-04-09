package com.ahmedyousri.boilerplate.springboot.banking.account.controller;

import com.ahmedyousri.boilerplate.springboot.api.AccountsApi;
import com.ahmedyousri.boilerplate.springboot.banking.account.service.AccountService;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountBalanceResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountCloseRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountCreationRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountDetailResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountStatementsResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountUpdateRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountsListResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AccountController implements AccountsApi {
    
    private final AccountService accountService;
    
    @Override
    public ResponseEntity<MessageResponse> _closeAccount(UUID accountId, AccountCloseRequest accountCloseRequest) {
        MessageResponse response = accountService.closeAccount(accountId, accountCloseRequest);
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<AccountResponse> _createAccount(AccountCreationRequest accountCreationRequest) {
        AccountResponse response = accountService.createAccount(accountCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Override
    public ResponseEntity<Resource> _downloadAccountStatement(UUID accountId, UUID statementId) {
        Resource resource = accountService.downloadAccountStatement(accountId, statementId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"statement-" + statementId + ".pdf\"")
                .body(resource);
    }
    
    @Override
    public ResponseEntity<AccountBalanceResponse> _getAccountBalance(UUID accountId) {
        AccountBalanceResponse response = accountService.getAccountBalance(accountId);
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<AccountDetailResponse> _getAccountDetails(UUID accountId) {
        AccountDetailResponse response = accountService.getAccountDetails(accountId);
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<AccountStatementsResponse> _getAccountStatements(
            UUID accountId, Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
        AccountStatementsResponse response = accountService.getAccountStatements(
                accountId, 
                startDate.orElse(null), 
                endDate.orElse(null));
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<AccountsListResponse> _getCustomerAccounts(
            Optional<String> status, Optional<String> type) {
        AccountsListResponse response = accountService.getCustomerAccounts(
                status.orElse(null), 
                type.orElse(null));
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<AccountResponse> _updateAccountSettings(
            UUID accountId, AccountUpdateRequest accountUpdateRequest) {
        AccountResponse response = accountService.updateAccountSettings(accountId, accountUpdateRequest);
        return ResponseEntity.ok(response);
    }
}
