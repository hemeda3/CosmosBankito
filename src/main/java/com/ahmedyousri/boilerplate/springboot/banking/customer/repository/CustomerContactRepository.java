package com.ahmedyousri.boilerplate.springboot.banking.customer.repository;

import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.CustomerContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerContactRepository extends JpaRepository<CustomerContact, UUID> {
    
    List<CustomerContact> findByCustomer(Customer customer);
    
    Optional<CustomerContact> findByCustomerAndId(Customer customer, UUID contactId);
    
    List<CustomerContact> findByCustomerAndType(Customer customer, CustomerContact.ContactType type);
    
    Optional<CustomerContact> findByCustomerAndValue(Customer customer, String value);
    
    boolean existsByCustomerAndValue(Customer customer, String value);
}
