package com.ahmedyousri.boilerplate.springboot.banking.transfer.service;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.repository.AccountRepository;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.service.CurrentCustomerService;
import com.ahmedyousri.boilerplate.springboot.banking.exception.BusinessRuleException;
import com.ahmedyousri.boilerplate.springboot.banking.exception.ResourceNotFoundException;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.entity.RecurringTransfer;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.entity.Transfer;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.mapper.TransferMapper;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.repository.RecurringTransferRepository;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.repository.TransferRepository;
import com.ahmedyousri.boilerplate.springboot.model.generated.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {
    
    private static final Logger log = LoggerFactory.getLogger(TransferServiceImpl.class);
    
    private final CurrentCustomerService currentCustomerService;
    private final TransferRepository transferRepository;
    private final RecurringTransferRepository recurringTransferRepository;
    private final AccountRepository accountRepository;
    private final TransferMapper transferMapper;
    private final com.ahmedyousri.boilerplate.springboot.banking.account.service.AccountServiceImpl accountService;
    private final com.ahmedyousri.boilerplate.springboot.banking.transaction.service.TransactionService transactionService;
    private final com.ahmedyousri.boilerplate.springboot.banking.accounting.service.AccountingService accountingService;
    private final com.ahmedyousri.boilerplate.springboot.banking.blockchain.service.BlockchainService blockchainService;
    
    @Override
    @Transactional(readOnly = true)
    public TransfersListResponse getCustomerTransfers(
            String status, 
            String type, 
            LocalDate startDate, 
            LocalDate endDate, 
            Integer page, 
            Integer pageSize
    ) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        Pageable pageable = PageRequest.of(
                page != null ? page - 1 : 0, 
                pageSize != null ? pageSize : 20, 
                Sort.by("createdAt").descending()
        );
        
        List<Transfer> transfers;
        
        if (status != null && type != null) {
            transfers = transferRepository.findAll((root, query, cb) -> 
                cb.and(
                    cb.equal(root.get("customer"), customer),
                    cb.equal(root.get("status"), Transfer.TransferStatus.fromValue(status)),
                    cb.equal(root.get("type"), Transfer.TransferType.fromValue(type))
                ), 
                pageable
            ).getContent();
        } else if (status != null) {
            transfers = transferRepository.findAll((root, query, cb) -> 
                cb.and(
                    cb.equal(root.get("customer"), customer),
                    cb.equal(root.get("status"), Transfer.TransferStatus.fromValue(status))
                ), 
                pageable
            ).getContent();
        } else if (type != null) {
            transfers = transferRepository.findAll((root, query, cb) -> 
                cb.and(
                    cb.equal(root.get("customer"), customer),
                    cb.equal(root.get("type"), Transfer.TransferType.fromValue(type))
                ), 
                pageable
            ).getContent();
        } else {
            transfers = transferRepository.findAll((root, query, cb) -> 
                cb.equal(root.get("customer"), customer), 
                pageable
            ).getContent();
        }
        
        TransfersListResponse response = new TransfersListResponse();
        response.setTransfers(transferMapper.toTransferResponseList(transfers));
        
        return response;
    }
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransferResponse createTransfer(TransferCreationRequest request) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        // Validate source account
        Account sourceAccount = accountRepository.findById(request.getSourceAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getSourceAccountId()));
        
        // Verify account ownership
        if (!sourceAccount.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessRuleException("Account does not belong to the customer", "ACCOUNT_NOT_OWNED");
        }
        
        // Validate sufficient funds with a margin for fees if applicable
        BigDecimal transferAmount = new BigDecimal(request.getAmount());
        if (sourceAccount.getAvailableBalance().compareTo(transferAmount) < 0) {
            throw new BusinessRuleException("Insufficient funds", "INSUFFICIENT_FUNDS");
        }
        
        // Create transfer with PENDING status
        Transfer transfer = transferMapper.toTransfer(request, sourceAccount, customer);
        transfer.setStatus(Transfer.TransferStatus.PENDING);
        Transfer savedTransfer = transferRepository.save(transfer);
        
        // Use a consistent transaction ID for all operations
        UUID transactionId = savedTransfer.getId();
        
        try {
            // Create and execute blockchain command for transfer
            UUID destinationAccountId = null;
            try {
                destinationAccountId = UUID.fromString(transfer.getDestinationAccountNumber());
            } catch (IllegalArgumentException e) {
                // Not a UUID, might be an external account number
                log.debug("Destination account number is not a UUID: {}", transfer.getDestinationAccountNumber());
            }
            
            if (destinationAccountId != null) {
                // Internal transfer to another account in our system
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.TransferCommand command = 
                    new com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.TransferCommand(
                        sourceAccount.getId(),
                        destinationAccountId,
                        transfer.getAmount(),
                        "Transfer: " + (transfer.getDescription() != null ? transfer.getDescription() : ""),
                        transfer.getCurrencyCode(),
                        transactionId
                    );
                
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.BlockchainResponse response = 
                    blockchainService.executeCommand(command);
                
                if (!response.isSuccessful()) {
                    throw new BusinessRuleException(
                        "Transfer failed on blockchain: " + response.getErrorMessage(),
                        response.getErrorCode()
                    );
                }
                
                log.info("Blockchain transfer successful with transaction hash: {}", response.getTransactionHash());
            } else {
                // External transfer, use withdraw command
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.WithdrawCommand command = 
                    new com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.WithdrawCommand(
                        sourceAccount.getId(),
                        transfer.getAmount(),
                        "External Transfer to " + transfer.getDestinationAccountNumber()
                    );
                
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.BlockchainResponse response = 
                    blockchainService.executeCommand(command);
                
                if (!response.isSuccessful()) {
                    throw new BusinessRuleException(
                        "Transfer failed on blockchain: " + response.getErrorMessage(),
                        response.getErrorCode()
                    );
                }
                
                log.info("Blockchain withdrawal for external transfer successful with transaction hash: {}", 
                        response.getTransactionHash());
            }
            
            // 1. Perform debit operation on source account
            accountService.performDebit(sourceAccount, transfer.getAmount());
            
            // 2. Create journal entry for the transfer
            accountingService.createWithdrawalJournalEntry(
                    sourceAccount, 
                    transfer.getAmount(), 
                    "Transfer to " + transfer.getDestinationAccountNumber(),
                    transactionId
            );
            
            // 3. Record debit transaction
            transactionService.recordTransaction(
                    sourceAccount,
                    com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.TransactionType.DEBIT,
                    transfer.getAmount(),
                    "Transfer to " + transfer.getDestinationAccountNumber(),
                    String.valueOf(transactionId)
            );
            
            // 4. Save the updated account
            accountRepository.save(sourceAccount);
            
            // 5. Update transfer status to COMPLETED
            savedTransfer.setStatus(Transfer.TransferStatus.COMPLETED);
            savedTransfer.setCompletedAt(LocalDateTime.now());
            Transfer completedTransfer = transferRepository.save(savedTransfer);
            
            log.info("Created transfer: {} for customer: {}", savedTransfer.getId(), customer.getId());
            
            return transferMapper.toTransferResponse(completedTransfer);
        } catch (Exception e) {
            // Mark the transfer as FAILED without rolling back the transaction
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            
            savedTransfer.setStatus(Transfer.TransferStatus.FAILED);
            transferRepository.save(savedTransfer);
            
            log.error("Failed to process transfer: {}", e.getMessage(), e);
            throw new BusinessRuleException("Transfer failed: " + e.getMessage(), "TRANSFER_FAILED");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public TransferDetailResponse getTransferDetails(UUID transferId) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        Transfer transfer = transferRepository.findByCustomerAndId(customer, transferId)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer", "id", transferId));
        
        return transferMapper.toTransferDetailResponse(transfer);
    }
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public MessageResponse cancelTransfer(UUID transferId, TransferCancelRequest request) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        Transfer transfer = transferRepository.findByCustomerAndId(customer, transferId)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer", "id", transferId));
        
        // Check if transfer can be cancelled
        if (transfer.getStatus() != Transfer.TransferStatus.PENDING && 
            transfer.getStatus() != Transfer.TransferStatus.SCHEDULED) {
            throw new BusinessRuleException(
                    "Transfer cannot be cancelled", 
                    "TRANSFER_CANNOT_BE_CANCELLED");
        }
        
        // Update transfer status
        transfer.setStatus(Transfer.TransferStatus.CANCELLED);
        transfer.setCancellationReason(request.getReason());
        
        // Refund amount to source account if already deducted
        if (transfer.getStatus() == Transfer.TransferStatus.PENDING) {
            Account sourceAccount = transfer.getSourceAccount();
            
            // Execute blockchain deposit command for the refund
            com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.DepositCommand command = 
                new com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.DepositCommand(
                    sourceAccount.getId(),
                    transfer.getAmount(),
                    "Refund for cancelled transfer " + transferId,
                    sourceAccount.getCurrencyCode()
                );
            
            com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.BlockchainResponse response = 
                blockchainService.executeCommand(command);
            
            if (!response.isSuccessful()) {
                log.warn("Failed to process refund on blockchain: {} - {}", 
                        response.getErrorCode(), response.getErrorMessage());
                // Continue with the refund in the database even if blockchain fails
            } else {
                log.info("Blockchain refund successful with transaction hash: {}", response.getTransactionHash());
            }
            
            // Credit the account with the refunded amount
            accountService.performCredit(sourceAccount, transfer.getAmount());
            
            // Create journal entry for the refund (double-entry accounting)
            accountingService.createDepositJournalEntry(
                    sourceAccount, 
                    transfer.getAmount(), 
                    "Refund for cancelled transfer " + transferId,
                    transfer.getId()
            );
            
            // Record credit transaction for the refund
            transactionService.recordTransaction(
                    sourceAccount,
                    com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.TransactionType.CREDIT,
                    transfer.getAmount(),
                    "Refund for cancelled transfer " + transferId,
                    String.valueOf(transfer.getId())
            );
            
            // Save the updated account
            accountRepository.save(sourceAccount);
        }
        
        transferRepository.save(transfer);
        
        log.info("Cancelled transfer: {} for customer: {}", transferId, customer.getId());
        
        MessageResponse response = new MessageResponse();
        response.setMessage("Transfer cancelled successfully");
        return response;
    }
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TransferResponse scheduleTransfer(ScheduledTransferRequest request) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        // Validate source account
        Account sourceAccount = accountRepository.findById(request.getSourceAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getSourceAccountId()));
        
        // Verify that the account belongs to the customer
        if (!sourceAccount.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessRuleException(
                    "Account does not belong to the customer", 
                    "ACCOUNT_NOT_OWNED");
        }
        
        // Validate account balance
        if (sourceAccount.getAvailableBalance().compareTo(new BigDecimal(request.getAmount())) < 0) {
            throw new BusinessRuleException(
                    "Insufficient funds", 
                    "INSUFFICIENT_FUNDS");
        }
        
        // Create transfer
        Transfer transfer = Transfer.builder()
                .sourceAccount(sourceAccount)
                .destinationAccountNumber(request.getDestinationAccountId() != null ? 
                    request.getDestinationAccountId().toString() : 
                    (request.getPayeeId() != null ? "Payee-" + request.getPayeeId() : null))
                .destinationBankCode(null) // No getBankCode() method
                .amount(new BigDecimal(request.getAmount()))
                .currencyCode(request.getCurrencyCode())
                .type(Transfer.TransferType.SCHEDULED)
                .status(Transfer.TransferStatus.SCHEDULED)
                .description(request.getReference())
                .scheduledFor(LocalDateTime.from(request.getScheduledDate()))
                .customer(customer)
                .build();
        
        // Save transfer first to get ID
        Transfer savedTransfer = transferRepository.save(transfer);
        
        try {
            // Create and execute blockchain command for scheduled transfer
            UUID destinationAccountId = null;
            try {
                destinationAccountId = UUID.fromString(transfer.getDestinationAccountNumber());
            } catch (IllegalArgumentException e) {
                // Not a UUID, might be an external account number
                log.debug("Destination account number is not a UUID: {}", transfer.getDestinationAccountNumber());
            }
            
            if (destinationAccountId != null) {
                // Internal transfer to another account in our system
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.TransferCommand command = 
                    new com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.TransferCommand(
                        sourceAccount.getId(),
                        destinationAccountId,
                        transfer.getAmount(),
                        "Scheduled Transfer: " + (transfer.getDescription() != null ? transfer.getDescription() : ""),
                        transfer.getCurrencyCode(),
                        savedTransfer.getId()
                    );
                
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.BlockchainResponse response = 
                    blockchainService.executeCommand(command);
                
                if (!response.isSuccessful()) {
                    throw new BusinessRuleException(
                        "Scheduled transfer failed on blockchain: " + response.getErrorMessage(),
                        response.getErrorCode()
                    );
                }
                
                log.info("Blockchain scheduled transfer successful with transaction hash: {}", response.getTransactionHash());
            } else {
                // External transfer, use withdraw command
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.WithdrawCommand command = 
                    new com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.WithdrawCommand(
                        sourceAccount.getId(),
                        transfer.getAmount(),
                        "Scheduled External Transfer to " + transfer.getDestinationAccountNumber()
                    );
                
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.BlockchainResponse response = 
                    blockchainService.executeCommand(command);
                
                if (!response.isSuccessful()) {
                    throw new BusinessRuleException(
                        "Scheduled transfer failed on blockchain: " + response.getErrorMessage(),
                        response.getErrorCode()
                    );
                }
                
                log.info("Blockchain withdrawal for scheduled external transfer successful with transaction hash: {}", 
                        response.getTransactionHash());
            }
            
            // Perform debit operation
            accountService.performDebit(sourceAccount, transfer.getAmount());
            
            // Create journal entry for the scheduled transfer (double-entry accounting)
            accountingService.createWithdrawalJournalEntry(
                    sourceAccount, 
                    transfer.getAmount(), 
                    "Scheduled Transfer to " + transfer.getDestinationAccountNumber(),
                    savedTransfer.getId()
            );
            
            // Record transaction
            transactionService.recordTransaction(
                    sourceAccount,
                    com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.TransactionType.DEBIT,
                    transfer.getAmount(),
                    "Scheduled Transfer to " + transfer.getDestinationAccountNumber(),
                    String.valueOf(savedTransfer.getId())
            );
            
            // Save the updated account
            accountRepository.save(sourceAccount);
        } catch (Exception e) {
            // If any exception occurs, mark the transfer as FAILED
            savedTransfer.setStatus(Transfer.TransferStatus.FAILED);
            transferRepository.save(savedTransfer);
            
            log.error("Failed to process scheduled transfer: {}", e.getMessage());
            throw e;
        }
        
        log.info("Scheduled transfer: {} for customer: {}", savedTransfer.getId(), customer.getId());
        
        return transferMapper.toTransferResponse(savedTransfer);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TransfersListResponse getScheduledTransfers() {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        List<Transfer> scheduledTransfers = transferRepository
                .findByCustomerAndStatusOrderByCreatedAtDesc(
                        customer, 
                        Transfer.TransferStatus.SCHEDULED
                );
        
        TransfersListResponse response = new TransfersListResponse();
        response.setTransfers(transferMapper.toTransferResponseList(scheduledTransfers));
        
        return response;
    }
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public RecurringTransferResponse createRecurringTransfer(RecurringTransferRequest request) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        // Validate source account
        Account sourceAccount = accountRepository.findById(request.getSourceAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getSourceAccountId()));
        
        // Verify that the account belongs to the customer
        if (!sourceAccount.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessRuleException(
                    "Account does not belong to the customer", 
                    "ACCOUNT_NOT_OWNED");
        }
        
        // Validate account balance
        if (sourceAccount.getAvailableBalance().compareTo(new BigDecimal(request.getAmount())) < 0) {
            throw new BusinessRuleException(
                    "Insufficient funds", 
                    "INSUFFICIENT_FUNDS");
        }
        
        // Create recurring transfer
        RecurringTransfer recurringTransfer = transferMapper.toRecurringTransfer(request, sourceAccount, customer);
        
        RecurringTransfer savedRecurringTransfer = recurringTransferRepository.save(recurringTransfer);
        
        log.info("Created recurring transfer: {} for customer: {}", 
                savedRecurringTransfer.getId(), customer.getId());
        
        return transferMapper.toRecurringTransferResponse(savedRecurringTransfer);
    }
    
    @Override
    @Transactional(readOnly = true)
    public RecurringTransfersListResponse getRecurringTransfers() {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        List<RecurringTransfer> recurringTransfers = recurringTransferRepository
                .findByCustomerAndStatusOrderByCreatedAtDesc(
                        customer, 
                        RecurringTransfer.RecurringTransferStatus.ACTIVE
                );
        
        RecurringTransfersListResponse response = new RecurringTransfersListResponse();
        response.setRecurringTransfers(transferMapper.toRecurringTransferResponseList(recurringTransfers));
        
        return response;
    }
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public RecurringTransferResponse updateRecurringTransfer(
            UUID recurringTransferId, 
            RecurringTransferUpdateRequest request
    ) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        RecurringTransfer recurringTransfer = recurringTransferRepository
                .findByCustomerAndId(customer, recurringTransferId)
                .orElseThrow(() -> new ResourceNotFoundException("RecurringTransfer", "id", recurringTransferId));
        
        // Update recurring transfer details
        // RecurringTransferUpdateRequest doesn't have destination account methods
        
        if (request.getAmount() != null) {
            recurringTransfer.setAmount(new BigDecimal(request.getAmount()));
            // No currency code in update request, keep existing
        }
        
        if (request.getFrequency() != null) {
            recurringTransfer.setFrequency(
                RecurringTransfer.RecurrenceFrequency.fromValue(request.getFrequency().getValue())
            );
        }
        
        // RecurringTransferUpdateRequest has endDate but not startDate
        if (request.getEndDate() != null) {
            recurringTransfer.setEndDate(request.getEndDate().toLocalDate());
        }
        
        if (request.getReference() != null) {
            recurringTransfer.setDescription(request.getReference());
        }
        
        if (request.getDescription() != null) {
            // Additional description field
            recurringTransfer.setDescription(request.getDescription());
        }
        
        RecurringTransfer updatedRecurringTransfer = recurringTransferRepository.save(recurringTransfer);
        
        log.info("Updated recurring transfer: {} for customer: {}", 
                updatedRecurringTransfer.getId(), customer.getId());
        
        return transferMapper.toRecurringTransferResponse(updatedRecurringTransfer);
    }
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void cancelRecurringTransfer(UUID recurringTransferId) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        RecurringTransfer recurringTransfer = recurringTransferRepository
                .findByCustomerAndId(customer, recurringTransferId)
                .orElseThrow(() -> new ResourceNotFoundException("RecurringTransfer", "id", recurringTransferId));
        
        // Update recurring transfer status
        recurringTransfer.setStatus(RecurringTransfer.RecurringTransferStatus.CANCELLED);
        
        recurringTransferRepository.save(recurringTransfer);
        
        log.info("Cancelled recurring transfer: {} for customer: {}", 
                recurringTransferId, customer.getId());
    }
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Transfer executeRecurringTransfer(RecurringTransfer recurringTransfer) {
        log.info("Executing recurring transfer: {}", recurringTransfer.getId());
        
        // Validate source account
        Account sourceAccount = recurringTransfer.getSourceAccount();
        if (sourceAccount == null) {
            throw new BusinessRuleException(
                    "Source account not found for recurring transfer", 
                    "SOURCE_ACCOUNT_NOT_FOUND");
        }
        
        // Validate account balance
        if (sourceAccount.getAvailableBalance().compareTo(recurringTransfer.getAmount()) < 0) {
            throw new BusinessRuleException(
                    "Insufficient funds for recurring transfer", 
                    "INSUFFICIENT_FUNDS");
        }
        
        // Create transfer
        Transfer transfer = Transfer.builder()
                .sourceAccount(sourceAccount)
                .destinationAccountNumber(recurringTransfer.getDestinationAccountNumber())
                .destinationBankCode(recurringTransfer.getDestinationBankCode())
                .amount(recurringTransfer.getAmount())
                .currencyCode(recurringTransfer.getCurrencyCode())
                .type(Transfer.TransferType.RECURRING)
                .status(Transfer.TransferStatus.PENDING)
                .description(recurringTransfer.getDescription() + " (Recurring)")
                .customer(recurringTransfer.getCustomer())
                .referenceId(recurringTransfer.getId().toString())
                .build();
        
        // Save transfer first to get ID
        Transfer savedTransfer = transferRepository.save(transfer);
        
        try {
            // Create and execute blockchain command for transfer
            UUID destinationAccountId = null;
            try {
                destinationAccountId = UUID.fromString(transfer.getDestinationAccountNumber());
            } catch (IllegalArgumentException e) {
                // Not a UUID, might be an external account number
                log.debug("Destination account number is not a UUID: {}", transfer.getDestinationAccountNumber());
            }
            
            if (destinationAccountId != null) {
                // Internal transfer to another account in our system
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.TransferCommand command = 
                    new com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.TransferCommand(
                        sourceAccount.getId(),
                        destinationAccountId,
                        transfer.getAmount(),
                        "Recurring Transfer: " + (transfer.getDescription() != null ? transfer.getDescription() : ""),
                        transfer.getCurrencyCode(),
                        savedTransfer.getId()
                    );
                
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.BlockchainResponse response = 
                    blockchainService.executeCommand(command);
                
                if (!response.isSuccessful()) {
                    throw new BusinessRuleException(
                        "Recurring transfer failed on blockchain: " + response.getErrorMessage(),
                        response.getErrorCode()
                    );
                }
                
                log.info("Blockchain recurring transfer successful with transaction hash: {}", response.getTransactionHash());
            } else {
                // External transfer, use withdraw command
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.WithdrawCommand command = 
                    new com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.WithdrawCommand(
                        sourceAccount.getId(),
                        transfer.getAmount(),
                        "Recurring External Transfer to " + transfer.getDestinationAccountNumber()
                    );
                
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.BlockchainResponse response = 
                    blockchainService.executeCommand(command);
                
                if (!response.isSuccessful()) {
                    throw new BusinessRuleException(
                        "Recurring transfer failed on blockchain: " + response.getErrorMessage(),
                        response.getErrorCode()
                    );
                }
                
                log.info("Blockchain withdrawal for recurring external transfer successful with transaction hash: {}", 
                        response.getTransactionHash());
            }
            
            // Perform debit operation
            accountService.performDebit(sourceAccount, transfer.getAmount());
            
            // Create journal entry for the recurring transfer (double-entry accounting)
            accountingService.createWithdrawalJournalEntry(
                    sourceAccount, 
                    transfer.getAmount(), 
                    "Recurring Transfer to " + transfer.getDestinationAccountNumber(),
                    savedTransfer.getId()
            );
            
            // Record transaction
            transactionService.recordTransaction(
                    sourceAccount,
                    com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.TransactionType.DEBIT,
                    transfer.getAmount(),
                    "Recurring Transfer to " + transfer.getDestinationAccountNumber(),
                    String.valueOf(savedTransfer.getId())
            );
            
            // Save the updated account
            accountRepository.save(sourceAccount);
            
            // Update transfer status to COMPLETED
            savedTransfer.setStatus(Transfer.TransferStatus.COMPLETED);
            savedTransfer.setCompletedAt(LocalDateTime.now());
            Transfer completedTransfer = transferRepository.save(savedTransfer);
            
            log.info("Executed recurring transfer: {} for customer: {}", 
                    savedTransfer.getId(), recurringTransfer.getCustomer().getId());
            
            return completedTransfer;
        } catch (Exception e) {
            // If any exception occurs, mark the transfer as FAILED
            savedTransfer.setStatus(Transfer.TransferStatus.FAILED);
            transferRepository.save(savedTransfer);
            
            log.error("Failed to process recurring transfer: {}", e.getMessage(), e);
            throw new BusinessRuleException("Recurring transfer failed: " + e.getMessage(), "TRANSFER_FAILED");
        }
    }
}
