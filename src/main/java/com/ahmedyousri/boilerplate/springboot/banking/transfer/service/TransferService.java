package com.ahmedyousri.boilerplate.springboot.banking.transfer.service;

import com.ahmedyousri.boilerplate.springboot.banking.transfer.entity.Transfer;
import com.ahmedyousri.boilerplate.springboot.model.generated.*;

import java.time.LocalDate;
import java.util.UUID;

public interface TransferService {
    
    /**
     * Get a list of customer transfers
     * 
     * @param status Optional filter by transfer status
     * @param type Optional filter by transfer type
     * @param startDate Optional start date for filtering transfers
     * @param endDate Optional end date for filtering transfers
     * @param page Optional page number for pagination
     * @param pageSize Optional number of items per page
     * @return List of transfers
     */
    TransfersListResponse getCustomerTransfers(
        String status, 
        String type, 
        LocalDate startDate, 
        LocalDate endDate, 
        Integer page, 
        Integer pageSize
    );
    
    /**
     * Create a new transfer
     * 
     * @param request Transfer creation details
     * @return Created transfer details
     */
    TransferResponse createTransfer(TransferCreationRequest request);
    
    /**
     * Get details of a specific transfer
     * 
     * @param transferId ID of the transfer
     * @return Transfer details
     */
    TransferDetailResponse getTransferDetails(UUID transferId);
    
    /**
     * Cancel a transfer
     * 
     * @param transferId ID of the transfer to cancel
     * @param request Cancellation details
     * @return Cancellation confirmation message
     */
    MessageResponse cancelTransfer(UUID transferId, TransferCancelRequest request);
    
    /**
     * Schedule a transfer for future execution
     * 
     * @param request Scheduled transfer details
     * @return Scheduled transfer details
     */
    TransferResponse scheduleTransfer(ScheduledTransferRequest request);
    
    /**
     * Get a list of scheduled transfers
     * 
     * @return List of scheduled transfers
     */
    TransfersListResponse getScheduledTransfers();
    
    /**
     * Create a recurring transfer
     * 
     * @param request Recurring transfer details
     * @return Created recurring transfer details
     */
    RecurringTransferResponse createRecurringTransfer(RecurringTransferRequest request);
    
    /**
     * Get a list of recurring transfers
     * 
     * @return List of recurring transfers
     */
    RecurringTransfersListResponse getRecurringTransfers();
    
    /**
     * Update an existing recurring transfer
     * 
     * @param recurringTransferId ID of the recurring transfer to update
     * @param request Update details
     * @return Updated recurring transfer details
     */
    RecurringTransferResponse updateRecurringTransfer(
        UUID recurringTransferId, 
        RecurringTransferUpdateRequest request
    );
    
    /**
     * Cancel a recurring transfer
     * 
     * @param recurringTransferId ID of the recurring transfer to cancel
     */
    void cancelRecurringTransfer(UUID recurringTransferId);
    
    /**
     * Execute a recurring transfer
     * 
     * @param recurringTransfer The recurring transfer to execute
     * @return The executed transfer
     */
    Transfer executeRecurringTransfer(com.ahmedyousri.boilerplate.springboot.banking.transfer.entity.RecurringTransfer recurringTransfer);
}
