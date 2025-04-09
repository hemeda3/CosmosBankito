package com.ahmedyousri.boilerplate.springboot.banking.reconciliation.service;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.repository.AccountRepository;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.service.AccountingService;
import com.ahmedyousri.boilerplate.springboot.banking.exception.ResourceNotFoundException;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for reconciling account balances with accounting journal entries.
 * This service ensures data consistency between the account balances and the accounting system.
 */
@Service
@RequiredArgsConstructor
public class ReconciliationService {

    private static final Logger log = LoggerFactory.getLogger(ReconciliationService.class);

    private final AccountRepository accountRepository;
    private final AccountingService accountingService;
    private final TransactionRepository transactionRepository;
    
    /**
     * Validates that an account's balance matches the calculated balance from journals.
     * 
     * @param accountId The ID of the account to verify
     * @return True if the account balance matches the accounting balance, false otherwise
     * @throws ResourceNotFoundException If the account does not exist
     */
    @Transactional(readOnly = true)
    public boolean verifyAccountBalance(UUID accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        
        // Get balance from accounting system
        BigDecimal accountingBalance = accountingService.getAccountBalanceFromJournalEntries(account);
        
        // Compare with current balance
        boolean isBalanced = account.getCurrentBalance().compareTo(accountingBalance) == 0;
        
        if (!isBalanced) {
            log.warn("Account balance mismatch for account {}: Current balance = {}, Accounting balance = {}", 
                    accountId, account.getCurrentBalance(), accountingBalance);
        }
        
        return isBalanced;
    }
    
    /**
     * Reconciles all accounts to ensure balance consistency.
     * This method is scheduled to run hourly.
     */
    @Scheduled(cron = "0 0 * * * *") // Run hourly
    @Transactional(readOnly = true)
    public void reconcileAllAccounts() {
        log.info("Starting scheduled account reconciliation");
        
        List<Account> accounts = accountRepository.findAll();
        List<Account> inconsistentAccounts = new ArrayList<>();
        
        for (Account account : accounts) {
            if (!verifyAccountBalance(account.getId())) {
                inconsistentAccounts.add(account);
                log.warn("Account balance inconsistency detected for account: {}", account.getId());
            }
        }
        
        if (!inconsistentAccounts.isEmpty()) {
            log.error("Found {} accounts with balance inconsistencies", inconsistentAccounts.size());
            // In a real system, this would trigger alerts to system administrators
            // and possibly initiate an automated correction process
        } else {
            log.info("Account reconciliation completed successfully. All {} accounts are balanced.", accounts.size());
        }
    }
    
    /**
     * Performs a detailed reconciliation for a specific account.
     * This method analyzes transactions and journal entries to identify discrepancies.
     * 
     * @param accountId The ID of the account to reconcile
     * @return A summary of the reconciliation results
     */
    @Transactional(readOnly = true)
    public String performDetailedReconciliation(UUID accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        
        BigDecimal accountingBalance = accountingService.getAccountBalanceFromJournalEntries(account);
        BigDecimal currentBalance = account.getCurrentBalance();
        
        StringBuilder report = new StringBuilder();
        report.append("Reconciliation Report for Account: ").append(account.getAccountNumber()).append("\n");
        report.append("Current Balance: ").append(currentBalance).append(" ").append(account.getCurrencyCode()).append("\n");
        report.append("Accounting Balance: ").append(accountingBalance).append(" ").append(account.getCurrencyCode()).append("\n");
        
        if (currentBalance.compareTo(accountingBalance) == 0) {
            report.append("Status: BALANCED\n");
        } else {
            BigDecimal discrepancy = currentBalance.subtract(accountingBalance);
            report.append("Status: DISCREPANCY FOUND\n");
            report.append("Discrepancy Amount: ").append(discrepancy).append(" ").append(account.getCurrencyCode()).append("\n");
            
            // In a real system, this would include detailed analysis of transactions and journal entries
            // to identify the specific source of the discrepancy
        }
        
        return report.toString();
    }
    
    /**
     * Performs a full reconciliation of all accounts.
     * This method is used by the batch service for end-of-day processing.
     * 
     * @return A summary of the reconciliation results
     */
    @Transactional
    public String performReconciliation() {
        log.info("Starting full reconciliation");
        
        List<Account> accounts = accountRepository.findAll();
        List<Account> inconsistentAccounts = new ArrayList<>();
        
        for (Account account : accounts) {
            if (!verifyAccountBalance(account.getId())) {
                inconsistentAccounts.add(account);
                log.warn("Account balance inconsistency detected for account: {}", account.getId());
            }
        }
        
        StringBuilder report = new StringBuilder();
        report.append("Reconciliation Report\n");
        report.append("Total Accounts: ").append(accounts.size()).append("\n");
        report.append("Balanced Accounts: ").append(accounts.size() - inconsistentAccounts.size()).append("\n");
        report.append("Inconsistent Accounts: ").append(inconsistentAccounts.size()).append("\n");
        
        if (!inconsistentAccounts.isEmpty()) {
            report.append("\nAccounts with Discrepancies:\n");
            for (Account account : inconsistentAccounts) {
                BigDecimal accountingBalance = accountingService.getAccountBalanceFromJournalEntries(account);
                BigDecimal currentBalance = account.getCurrentBalance();
                BigDecimal discrepancy = currentBalance.subtract(accountingBalance);
                
                report.append("Account: ").append(account.getAccountNumber())
                      .append(", Current Balance: ").append(currentBalance)
                      .append(", Accounting Balance: ").append(accountingBalance)
                      .append(", Discrepancy: ").append(discrepancy)
                      .append("\n");
            }
            
            log.error("Found {} accounts with balance inconsistencies", inconsistentAccounts.size());
            // In a real system, this would trigger alerts to system administrators
            // and possibly initiate an automated correction process
        } else {
            log.info("Reconciliation completed successfully. All {} accounts are balanced.", accounts.size());
        }
        
        return report.toString();
    }
}
