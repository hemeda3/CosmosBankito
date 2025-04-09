package com.ahmedyousri.boilerplate.springboot.banking.compensation.service;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.repository.AccountRepository;
import com.ahmedyousri.boilerplate.springboot.banking.account.service.AccountService;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.service.AccountingService;
import com.ahmedyousri.boilerplate.springboot.banking.audit.service.AuditService;
import com.ahmedyousri.boilerplate.springboot.banking.exception.ResourceNotFoundException;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.TransactionType;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.service.TransactionService;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.entity.Transfer;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for compensating failed transactions.
 * This service provides methods for handling error recovery through compensating transactions.
 */
@Service
@RequiredArgsConstructor
public class CompensationService {
    
    private static final Logger log = LoggerFactory.getLogger(CompensationService.class);
    
    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final AccountingService accountingService;
    private final TransactionService transactionService;
    private final AuditService auditService;
    
    /**
     * Compensate a failed transfer.
     *
     * @param transferId The ID of the failed transfer
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void compensateFailedTransfer(UUID transferId) {
        log.info("Compensating failed transfer: {}", transferId);
        
        Transfer failedTransfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer", "id", transferId));
        
        if (failedTransfer.getStatus() != Transfer.TransferStatus.FAILED) {
            throw new IllegalStateException("Cannot compensate a transfer that is not in FAILED state");
        }
        
        // Check if a compensation has already been performed
        if (isRefundRequired(failedTransfer)) {
            performCompensation(failedTransfer);
        } else {
            log.info("Compensation already performed for transfer: {}", transferId);
        }
    }
    
    /**
     * Check if a refund is required for a failed transfer.
     *
     * @param transfer The failed transfer
     * @return True if a refund is required, false otherwise
     */
    private boolean isRefundRequired(Transfer transfer) {
        // Check if a compensation transfer already exists
        return !transferRepository.existsByReferenceIdAndType(
                transfer.getId().toString(), Transfer.TransferType.COMPENSATION);
    }
    
    /**
     * Perform compensation for a failed transfer.
     *
     * @param failedTransfer The failed transfer
     */
    private void performCompensation(Transfer failedTransfer) {
        log.info("Performing compensation for transfer: {}", failedTransfer.getId());
        
        Account sourceAccount = failedTransfer.getSourceAccount();
        
        // Refund the source account
        accountService.performCredit(
                sourceAccount,
                failedTransfer.getAmount()
        );
        
        // Create compensating journal entry
        accountingService.createCompensatingJournalEntry(
                sourceAccount,
                failedTransfer.getAmount(),
                "Compensation for failed transfer " + failedTransfer.getId(),
                UUID.randomUUID()
        );
        
        // Record compensation transaction
        transactionService.recordTransaction(
                sourceAccount,
                TransactionType.CREDIT,
                failedTransfer.getAmount(),
                "Compensation for failed transfer " + failedTransfer.getId(),
                UUID.randomUUID().toString()
        );
        
        // Update transfer status
        failedTransfer.setStatus(Transfer.TransferStatus.COMPENSATED);
        transferRepository.save(failedTransfer);
        
        // Create a compensation transfer record
        Transfer compensationTransfer = new Transfer();
        compensationTransfer.setSourceAccount(failedTransfer.getDestinationAccount());
        compensationTransfer.setDestinationAccount(failedTransfer.getSourceAccount());
        compensationTransfer.setAmount(failedTransfer.getAmount());
        compensationTransfer.setCurrencyCode(failedTransfer.getCurrencyCode());
        compensationTransfer.setDescription("Compensation for failed transfer " + failedTransfer.getId());
        compensationTransfer.setType(Transfer.TransferType.COMPENSATION);
        compensationTransfer.setStatus(Transfer.TransferStatus.COMPLETED);
        compensationTransfer.setReferenceId(failedTransfer.getId().toString());
        
        transferRepository.save(compensationTransfer);
        
        // Log the compensation
        auditService.logFinancialOperation(
                "TRANSFER_COMPENSATION",
                sourceAccount.getId(),
                failedTransfer.getAmount(),
                "Compensation for failed transfer " + failedTransfer.getId(),
                "SYSTEM",
                true
        );
        
        log.info("Compensation completed for transfer: {}", failedTransfer.getId());
    }
    
    /**
     * Compensate a failed deposit.
     *
     * @param accountId   The ID of the account
     * @param description The description of the deposit
     * @param referenceId The reference ID of the deposit
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void compensateFailedDeposit(UUID accountId, UUID referenceId, String description) {
        log.info("Compensating failed deposit for account: {}, reference: {}", accountId, referenceId);
        
        // No action needed for failed deposits as no money was taken from the customer
        
        // Log the compensation
        auditService.logFinancialOperation(
                "DEPOSIT_COMPENSATION",
                accountId,
                null,
                description,
                "SYSTEM",
                true
        );
        
        log.info("Compensation completed for deposit: {}", referenceId);
    }
    
    /**
     * Compensate a failed withdrawal.
     *
     * @param accountId   The ID of the account
     * @param description The description of the withdrawal
     * @param referenceId The reference ID of the withdrawal
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void compensateFailedWithdrawal(UUID accountId, UUID referenceId, String description) {
        log.info("Compensating failed withdrawal for account: {}, reference: {}", accountId, referenceId);
        
        // No action needed for failed withdrawals as no money was given to the customer
        
        // Log the compensation
        auditService.logFinancialOperation(
                "WITHDRAWAL_COMPENSATION",
                accountId,
                null,
                description,
                "SYSTEM",
                true
        );
        
        log.info("Compensation completed for withdrawal: {}", referenceId);
    }
}
