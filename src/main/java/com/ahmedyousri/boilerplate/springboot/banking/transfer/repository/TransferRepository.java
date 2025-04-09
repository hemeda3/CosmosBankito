package com.ahmedyousri.boilerplate.springboot.banking.transfer.repository;

import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, UUID>, JpaSpecificationExecutor<Transfer> {
    
    List<Transfer> findByCustomer(Customer customer);
    
    List<Transfer> findByCustomerAndStatus(Customer customer, Transfer.TransferStatus status);
    
    List<Transfer> findByCustomerAndType(Customer customer, Transfer.TransferType type);
    
    List<Transfer> findByCustomerAndStatusAndType(
            Customer customer, 
            Transfer.TransferStatus status, 
            Transfer.TransferType type
    );
    
    List<Transfer> findByCustomerAndCreatedAtBetween(
            Customer customer, 
            LocalDateTime startDate, 
            LocalDateTime endDate
    );
    
    Optional<Transfer> findByCustomerAndId(Customer customer, UUID transferId);
    
    List<Transfer> findByCustomerAndStatusOrderByCreatedAtDesc(
            Customer customer, 
            Transfer.TransferStatus status
    );
    
    boolean existsByReferenceIdAndType(String referenceId, Transfer.TransferType type);
    
    Optional<Transfer> findByReferenceId(String referenceId);
}
