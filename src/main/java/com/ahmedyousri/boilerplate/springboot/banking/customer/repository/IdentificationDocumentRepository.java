package com.ahmedyousri.boilerplate.springboot.banking.customer.repository;

import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.IdentificationDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IdentificationDocumentRepository extends JpaRepository<IdentificationDocument, UUID> {
    
    List<IdentificationDocument> findByCustomer(Customer customer);
    
    Optional<IdentificationDocument> findByCustomerAndId(Customer customer, UUID documentId);
    
    List<IdentificationDocument> findByCustomerAndType(Customer customer, IdentificationDocument.DocumentType type);
    
    List<IdentificationDocument> findByCustomerAndVerificationStatus(
            Customer customer, 
            IdentificationDocument.DocumentVerificationStatus status);
    
    Optional<IdentificationDocument> findByCustomerAndDocumentNumber(Customer customer, String documentNumber);
    
    boolean existsByCustomerAndDocumentNumber(Customer customer, String documentNumber);
}
