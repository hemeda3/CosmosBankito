package com.ahmedyousri.boilerplate.springboot.banking.transfer.mapper;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.entity.RecurringTransfer;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.entity.Transfer;
import com.ahmedyousri.boilerplate.springboot.model.generated.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransferMapper {
    
    /**
     * Convert Transfer entity to TransferResponse DTO
     */
    public TransferResponse toTransferResponse(Transfer transfer) {
        if (transfer == null) {
            return null;
        }
        
        TransferResponse response = new TransferResponse();
        response.setId(transfer.getId());
        response.setSourceAccountId(transfer.getSourceAccount().getId());
        response.setDestinationAccountNumber(transfer.getDestinationAccountNumber());
        // The generated model doesn't have setDestinationBankCode or setBankCode methods
        // response.setDestinationBankCode(transfer.getDestinationBankCode());
        // response.setBankCode(transfer.getDestinationBankCode());
        
        Money amount = new Money();
        amount.setAmount(transfer.getAmount().toString());
        amount.setCurrencyCode(transfer.getCurrencyCode());
        response.setAmount(amount);
        
        response.setType(TransferResponse.TypeEnum.fromValue(transfer.getType().getValue()));
        response.setStatus(TransferResponse.StatusEnum.fromValue(transfer.getStatus().getValue()));
        // The generated model doesn't have setDescription or setNotes methods
        // response.setDescription(transfer.getDescription());
        // response.setNotes(transfer.getDescription());
        
        // The generated model doesn't have setCreatedAt or setCreationTime methods
        // response.setCreatedAt(toOffsetDateTime(transfer.getCreatedAt()));
        // response.setCreationTime(toOffsetDateTime(transfer.getCreatedAt()));
        response.setCompletedAt(transfer.getCompletedAt() != null ? toOffsetDateTime(transfer.getCompletedAt()) : null);
        
        return response;
    }
    
    /**
     * Convert Transfer entity to TransferDetailResponse DTO
     */
    public TransferDetailResponse toTransferDetailResponse(Transfer transfer) {
        if (transfer == null) {
            return null;
        }
        
        TransferDetailResponse response = new TransferDetailResponse();
        response.setId(transfer.getId());
        response.setSourceAccountId(transfer.getSourceAccount().getId());
        response.setDestinationAccountNumber(transfer.getDestinationAccountNumber());
        // The generated model doesn't have setDestinationBankCode or setBankCode methods
        // response.setDestinationBankCode(transfer.getDestinationBankCode());
        // response.setBankCode(transfer.getDestinationBankCode());
        
        Money amount = new Money();
        amount.setAmount(transfer.getAmount().toString());
        amount.setCurrencyCode(transfer.getCurrencyCode());
        response.setAmount(amount);
        
        response.setType(TransferDetailResponse.TypeEnum.fromValue(transfer.getType().getValue()));
        response.setStatus(TransferDetailResponse.StatusEnum.fromValue(transfer.getStatus().getValue()));
        // The generated model doesn't have setDescription or setNotes methods
        // response.setDescription(transfer.getDescription());
        // response.setNotes(transfer.getDescription());
        
        // The generated model doesn't have setCreatedAt or setCreationTime methods
        // response.setCreatedAt(toOffsetDateTime(transfer.getCreatedAt()));
        // response.setCreationTime(toOffsetDateTime(transfer.getCreatedAt()));
        
        // The generated model doesn't have setCompletedAt or setCompletionTime methods
        // response.setCompletedAt(transfer.getCompletedAt() != null ? toOffsetDateTime(transfer.getCompletedAt()) : null);
        // response.setCompletionTime(transfer.getCompletedAt() != null ? toOffsetDateTime(transfer.getCompletedAt()) : null);
        
        // The generated model doesn't have setScheduledAt or setScheduledTime methods
        // response.setScheduledAt(transfer.getScheduledFor() != null ? toOffsetDateTime(transfer.getScheduledFor()) : null);
        // response.setScheduledTime(transfer.getScheduledFor() != null ? toOffsetDateTime(transfer.getScheduledFor()) : null);
        // The generated model doesn't have setCancelReason or setCancellationReason methods
        // response.setCancellationReason(transfer.getCancellationReason());
        
        return response;
    }
    
    /**
     * Convert TransferCreationRequest DTO to Transfer entity
     */
    public Transfer toTransfer(TransferCreationRequest request, Account sourceAccount, Customer customer) {
        if (request == null) {
            return null;
        }
        
        return Transfer.builder()
                .sourceAccount(sourceAccount)
                .destinationAccountNumber(request.getDestinationAccountId() != null ? 
                    request.getDestinationAccountId().toString() : 
                    (request.getPayeeId() != null ? "Payee-" + request.getPayeeId() : null))
                .destinationBankCode(null) // getBankCode() method doesn't exist
                .amount(new BigDecimal(request.getAmount()))
                .currencyCode(request.getCurrencyCode())
                .type(Transfer.TransferType.fromValue(request.getType().getValue()))
                .status(Transfer.TransferStatus.PENDING)
                .description(request.getReference())
                .customer(customer)
                .build();
    }
    
    /**
     * Convert RecurringTransfer entity to RecurringTransferResponse DTO
     */
    public RecurringTransferResponse toRecurringTransferResponse(RecurringTransfer recurringTransfer) {
        if (recurringTransfer == null) {
            return null;
        }
        
        RecurringTransferResponse response = new RecurringTransferResponse();
        response.setId(recurringTransfer.getId());
        response.setSourceAccountId(recurringTransfer.getSourceAccount().getId());
        response.setDestinationAccountNumber(recurringTransfer.getDestinationAccountNumber());
        // The generated model doesn't have setDestinationBankCode or setBankCode methods
        // response.setDestinationBankCode(recurringTransfer.getDestinationBankCode());
        // response.setBankCode(recurringTransfer.getDestinationBankCode());
        
        Money amount = new Money();
        amount.setAmount(recurringTransfer.getAmount().toString());
        amount.setCurrencyCode(recurringTransfer.getCurrencyCode());
        response.setAmount(amount);
        
        response.setFrequency(RecurringTransferResponse.FrequencyEnum.fromValue(recurringTransfer.getFrequency().getValue()));
        response.setStatus(RecurringTransferResponse.StatusEnum.fromValue(recurringTransfer.getStatus().getValue()));
        // The generated model doesn't have setDescription or setNotes methods
        // response.setDescription(recurringTransfer.getDescription());
        // response.setNotes(recurringTransfer.getDescription());
        
        // Convert LocalDate to OffsetDateTime for the generated model
        response.setStartDate(recurringTransfer.getStartDate() != null ? 
            recurringTransfer.getStartDate().atStartOfDay().atOffset(ZoneOffset.UTC) : null);
        response.setEndDate(recurringTransfer.getEndDate() != null ? 
            recurringTransfer.getEndDate().atStartOfDay().atOffset(ZoneOffset.UTC) : null);
        
        // The generated model doesn't have setCreatedAt or setCreationTime methods
        // response.setCreatedAt(toOffsetDateTime(recurringTransfer.getCreatedAt()));
        // response.setCreationTime(toOffsetDateTime(recurringTransfer.getCreatedAt()));
        
        // The generated model doesn't have setLastExecutedAt or setLastExecutionTime methods
        // response.setLastExecutedAt(recurringTransfer.getLastExecutedAt() != null ? toOffsetDateTime(recurringTransfer.getLastExecutedAt()) : null);
        // response.setLastExecutionTime(recurringTransfer.getLastExecutedAt() != null ? toOffsetDateTime(recurringTransfer.getLastExecutedAt()) : null);
        
        // The generated model doesn't have setNextExecutionAt or setNextExecutionTime methods
        // response.setNextExecutionAt(recurringTransfer.getNextExecutionAt() != null ? toOffsetDateTime(recurringTransfer.getNextExecutionAt()) : null);
        // response.setNextExecutionTime(recurringTransfer.getNextExecutionAt() != null ? toOffsetDateTime(recurringTransfer.getNextExecutionAt()) : null);
        
        return response;
    }
    
    /**
     * Convert RecurringTransferRequest DTO to RecurringTransfer entity
     */
    public RecurringTransfer toRecurringTransfer(
            RecurringTransferRequest request, 
            Account sourceAccount, 
            Customer customer
    ) {
        if (request == null) {
            return null;
        }
        
        return RecurringTransfer.builder()
                .sourceAccount(sourceAccount)
                .destinationAccountNumber(request.getDestinationAccountId() != null ? 
                    request.getDestinationAccountId().toString() : 
                    (request.getPayeeId() != null ? "Payee-" + request.getPayeeId() : null))
                .destinationBankCode(null) // getBankCode() method doesn't exist
                .amount(new BigDecimal(request.getAmount()))
                .currencyCode(request.getCurrencyCode())
                .frequency(RecurringTransfer.RecurrenceFrequency.fromValue(request.getFrequency().getValue()))
                .status(RecurringTransfer.RecurringTransferStatus.ACTIVE)
                .description(request.getReference())
                .startDate(request.getStartDate() != null ? request.getStartDate().toLocalDate() : null)
                .endDate(request.getEndDate() != null ? request.getEndDate().toLocalDate() : null)
                .customer(customer)
                .build();
    }
    
    /**
     * Convert a list of Transfer entities to TransferResponse DTOs
     */
    public List<TransferResponse> toTransferResponseList(List<Transfer> transfers) {
        if (transfers == null) {
            return null;
        }
        
        return transfers.stream()
                .map(this::toTransferResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert a list of RecurringTransfer entities to RecurringTransferResponse DTOs
     */
    public List<RecurringTransferResponse> toRecurringTransferResponseList(List<RecurringTransfer> recurringTransfers) {
        if (recurringTransfers == null) {
            return null;
        }
        
        return recurringTransfers.stream()
                .map(this::toRecurringTransferResponse)
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
