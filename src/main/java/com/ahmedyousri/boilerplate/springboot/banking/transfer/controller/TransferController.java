package com.ahmedyousri.boilerplate.springboot.banking.transfer.controller;

import com.ahmedyousri.boilerplate.springboot.api.TransfersApi;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.service.TransferService;
import com.ahmedyousri.boilerplate.springboot.model.generated.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TransferController implements TransfersApi {
    
    private final TransferService transferService;
    
    @Override
    public ResponseEntity<Void> _cancelRecurringTransfer(UUID recurringTransferId) {
        transferService.cancelRecurringTransfer(recurringTransferId);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<MessageResponse> _cancelTransfer(
            UUID transferId, 
            TransferCancelRequest transferCancelRequest) {
        MessageResponse response = transferService.cancelTransfer(transferId, transferCancelRequest);
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<RecurringTransferResponse> _createRecurringTransfer(
            RecurringTransferRequest recurringTransferRequest) {
        RecurringTransferResponse response = transferService.createRecurringTransfer(recurringTransferRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Override
    public ResponseEntity<TransferResponse> _createTransfer(
            TransferCreationRequest transferCreationRequest) {
        TransferResponse response = transferService.createTransfer(transferCreationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Override
    public ResponseEntity<TransfersListResponse> _getCustomerTransfers(
            Optional<String> status, 
            Optional<String> type, 
            Optional<LocalDate> startDate, 
            Optional<LocalDate> endDate, 
            Optional<Integer> page, 
            Optional<Integer> pageSize) {
        TransfersListResponse response = transferService.getCustomerTransfers(
                status.orElse(null), 
                type.orElse(null), 
                startDate.orElse(null), 
                endDate.orElse(null), 
                page.orElse(null), 
                pageSize.orElse(null));
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<RecurringTransfersListResponse> _getRecurringTransfers() {
        RecurringTransfersListResponse response = transferService.getRecurringTransfers();
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<TransfersListResponse> _getScheduledTransfers() {
        TransfersListResponse response = transferService.getScheduledTransfers();
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<TransferDetailResponse> _getTransferDetails(UUID transferId) {
        TransferDetailResponse response = transferService.getTransferDetails(transferId);
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<TransferResponse> _scheduleTransfer(
            ScheduledTransferRequest scheduledTransferRequest) {
        TransferResponse response = transferService.scheduleTransfer(scheduledTransferRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Override
    public ResponseEntity<RecurringTransferResponse> _updateRecurringTransfer(
            UUID recurringTransferId, 
            RecurringTransferUpdateRequest recurringTransferUpdateRequest) {
        RecurringTransferResponse response = transferService.updateRecurringTransfer(
                recurringTransferId, recurringTransferUpdateRequest);
        return ResponseEntity.ok(response);
    }
}
