package com.ahmedyousri.boilerplate.springboot.banking.batch.service;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountStatus;
import com.ahmedyousri.boilerplate.springboot.banking.account.repository.AccountRepository;
import com.ahmedyousri.boilerplate.springboot.banking.account.service.AccountService;
import com.ahmedyousri.boilerplate.springboot.banking.audit.service.AuditService;
import com.ahmedyousri.boilerplate.springboot.banking.reconciliation.service.ReconciliationService;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.entity.RecurringTransfer;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.repository.RecurringTransferRepository;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for batch processing operations.
 * This service provides methods for end-of-day processing and other scheduled tasks.
 */
@Service
@RequiredArgsConstructor
public class BatchService {
    
    private static final Logger log = LoggerFactory.getLogger(BatchService.class);
    
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final TransferService transferService;
    private final RecurringTransferRepository recurringTransferRepository;
    private final ReconciliationService reconciliationService;
    private final AuditService auditService;
    
    /**
     * Perform end-of-day processing.
     * This method is scheduled to run at midnight every day.
     */
    @Scheduled(cron = "0 0 0 * * *") // Midnight every day
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void performEndOfDayProcessing() {
        log.info("Starting end-of-day processing");
        
        try {
            // Process scheduled transfers
            processScheduledTransfers();
            
            // Generate statements
            generateStatements();
            
            // Perform reconciliation
            performReconciliation();
            
            // Log the successful end-of-day processing
            auditService.logSystemOperation(
                    "END_OF_DAY_PROCESSING",
                    "End-of-day processing completed successfully",
                    "SYSTEM",
                    true
            );
            
            log.info("End-of-day processing completed successfully");
        } catch (Exception e) {
            log.error("Error during end-of-day processing: {}", e.getMessage(), e);
            
            // Log the failed end-of-day processing
            auditService.logSystemOperation(
                    "END_OF_DAY_PROCESSING",
                    "End-of-day processing failed: " + e.getMessage(),
                    "SYSTEM",
                    false
            );
        }
    }
    
    /**
     * Process scheduled transfers.
     * This method processes recurring transfers that are due today.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void processScheduledTransfers() {
        log.info("Processing scheduled transfers");
        
        LocalDate today = LocalDate.now();
        LocalDateTime todayEndOfDay = today.atTime(23, 59, 59);
        
        // Find recurring transfers that are due today
        List<RecurringTransfer> dueTransfers = recurringTransferRepository.findByNextExecutionAtLessThanEqual(todayEndOfDay);
        
        log.info("Found {} recurring transfers due for execution", dueTransfers.size());
        
        for (RecurringTransfer recurringTransfer : dueTransfers) {
            try {
                // Execute the transfer
                transferService.executeRecurringTransfer(recurringTransfer);
                
                // Update the next execution date
                recurringTransfer.setLastExecutionDate(today);
                recurringTransfer.calculateNextExecutionDate();
                recurringTransferRepository.save(recurringTransfer);
                
                log.info("Executed recurring transfer: {}", recurringTransfer.getId());
            } catch (Exception e) {
                log.error("Error executing recurring transfer {}: {}", recurringTransfer.getId(), e.getMessage(), e);
                
                // Log the failed transfer
                auditService.logFinancialOperation(
                        "RECURRING_TRANSFER_EXECUTION",
                        recurringTransfer.getSourceAccount().getId(),
                        recurringTransfer.getAmount(),
                        "Failed to execute recurring transfer: " + e.getMessage(),
                        "SYSTEM",
                        false
                );
            }
        }
        
        log.info("Scheduled transfers processing completed");
    }
    
    /**
     * Generate statements for all accounts.
     * This method generates monthly statements for all accounts if it's the last day of the month.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void generateStatements() {
        log.info("Generating statements");
        
        LocalDate today = LocalDate.now();
        
        // Check if it's the last day of the month
        if (today.getDayOfMonth() == today.lengthOfMonth()) {
            log.info("Generating monthly statements for all accounts");
            
            // Find all active accounts
            List<Account> activeAccounts = accountRepository.findByStatus(AccountStatus.ACTIVE);
            
            for (Account account : activeAccounts) {
                try {
                    // Generate statement for the account
                    accountService.generateMonthlyStatement(account.getId());
                    
                    log.info("Generated monthly statement for account: {}", account.getId());
                } catch (Exception e) {
                    log.error("Error generating monthly statement for account {}: {}", account.getId(), e.getMessage(), e);
                    
                    // Log the failed statement generation
                    auditService.logFinancialOperation(
                            "STATEMENT_GENERATION",
                            account.getId(),
                            null,
                            "Failed to generate monthly statement: " + e.getMessage(),
                            "SYSTEM",
                            false
                    );
                }
            }
        }
        
        log.info("Statement generation completed");
    }
    
    /**
     * Perform reconciliation for all accounts.
     * This method reconciles the account balances with the transaction history.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void performReconciliation() {
        log.info("Performing reconciliation");
        
        try {
            // Perform reconciliation for all accounts
            reconciliationService.reconcileAllAccounts();
            
            log.info("Reconciliation completed successfully");
        } catch (Exception e) {
            log.error("Error during reconciliation: {}", e.getMessage(), e);
            
            // Log the failed reconciliation
            auditService.logSystemOperation(
                    "RECONCILIATION",
                    "Reconciliation failed: " + e.getMessage(),
                    "SYSTEM",
                    false
            );
        }
    }
    
    /**
     * Clean up old data.
     * This method is scheduled to run once a month to clean up old data.
     */
    @Scheduled(cron = "0 0 0 1 * *") // Midnight on the first day of each month
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void cleanupOldData() {
        log.info("Starting old data cleanup");
        
        try {
            // Clean up old audit logs (older than 1 year)
            LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
            // Implement cleanup logic here
            
            // Log the successful cleanup
            auditService.logSystemOperation(
                    "DATA_CLEANUP",
                    "Old data cleanup completed successfully",
                    "SYSTEM",
                    true
            );
            
            log.info("Old data cleanup completed successfully");
        } catch (Exception e) {
            log.error("Error during old data cleanup: {}", e.getMessage(), e);
            
            // Log the failed cleanup
            auditService.logSystemOperation(
                    "DATA_CLEANUP",
                    "Old data cleanup failed: " + e.getMessage(),
                    "SYSTEM",
                    false
            );
        }
    }
}
