package com.ahmedyousri.boilerplate.springboot.banking.account.controller;

import com.ahmedyousri.boilerplate.springboot.banking.account.service.AccountService;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountBalanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Controller for account transaction operations.
 */
@RestController
@RequestMapping("/api/v1/accounts/{accountId}/transactions")
@RequiredArgsConstructor
public class AccountTransactionController {
    
    private final AccountService accountService;
    
    /**
     * Deposit money into an account.
     * 
     * @param accountId The account ID
     * @param amount The amount to deposit
     * @param description Optional description of the deposit
     * @return Updated account balance
     */
    @PostMapping("/deposit")
    public ResponseEntity<AccountBalanceResponse> deposit(
            @PathVariable UUID accountId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description) {
        
        AccountBalanceResponse response = accountService.deposit(accountId, amount, description);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Withdraw money from an account.
     * 
     * @param accountId The account ID
     * @param amount The amount to withdraw
     * @param description Optional description of the withdrawal
     * @return Updated account balance
     */
    @PostMapping("/withdraw")
    public ResponseEntity<AccountBalanceResponse> withdraw(
            @PathVariable UUID accountId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description) {
        
        AccountBalanceResponse response = accountService.withdraw(accountId, amount, description);
        return ResponseEntity.ok(response);
    }
}
