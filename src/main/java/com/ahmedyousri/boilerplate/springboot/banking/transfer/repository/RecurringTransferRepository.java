package com.ahmedyousri.boilerplate.springboot.banking.transfer.repository;

import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.entity.RecurringTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecurringTransferRepository extends JpaRepository<RecurringTransfer, UUID>, JpaSpecificationExecutor<RecurringTransfer> {
    
    List<RecurringTransfer> findByCustomer(Customer customer);
    
    List<RecurringTransfer> findByCustomerAndStatus(
            Customer customer, 
            RecurringTransfer.RecurringTransferStatus status
    );
    
    List<RecurringTransfer> findByCustomerAndFrequency(
            Customer customer, 
            RecurringTransfer.RecurrenceFrequency frequency
    );
    
    List<RecurringTransfer> findByCustomerAndStatusAndFrequency(
            Customer customer, 
            RecurringTransfer.RecurringTransferStatus status, 
            RecurringTransfer.RecurrenceFrequency frequency
    );
    
    Optional<RecurringTransfer> findByCustomerAndId(Customer customer, UUID recurringTransferId);
    
    List<RecurringTransfer> findByCustomerAndNextExecutionAtBefore(
            Customer customer, 
            LocalDateTime currentDateTime
    );
    
    List<RecurringTransfer> findByCustomerAndStatusOrderByCreatedAtDesc(
            Customer customer, 
            RecurringTransfer.RecurringTransferStatus status
    );
    
    List<RecurringTransfer> findByStatusAndNextExecutionAtBefore(
            RecurringTransfer.RecurringTransferStatus status,
            LocalDateTime currentDateTime
    );
    
    List<RecurringTransfer> findByStatusAndActive(
            RecurringTransfer.RecurringTransferStatus status,
            boolean active
    );
    
    /**
     * Find recurring transfers with next execution date less than or equal to the given date.
     * 
     * @param dateTime The date to compare with
     * @return List of recurring transfers that need to be executed
     */
    List<RecurringTransfer> findByNextExecutionAtLessThanEqual(LocalDateTime dateTime);
    
    /**
     * Find recurring transfers with next execution date less than or equal to the given date.
     * 
     * @param date The date to compare with
     * @return List of recurring transfers that need to be executed
     */
    default List<RecurringTransfer> findByNextExecutionDateLessThanEqual(LocalDate date) {
        return findByNextExecutionAtLessThanEqual(date.atTime(23, 59, 59));
    }
}
