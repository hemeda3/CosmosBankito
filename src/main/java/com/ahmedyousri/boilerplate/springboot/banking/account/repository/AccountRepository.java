package com.ahmedyousri.boilerplate.springboot.banking.account.repository;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountStatus;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountType;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    
    List<Account> findByCustomer(Customer customer);
    
    List<Account> findByCustomerAndStatus(Customer customer, AccountStatus status);
    
    List<Account> findByCustomerAndType(Customer customer, AccountType type);
    
    List<Account> findByCustomerAndStatusAndType(Customer customer, AccountStatus status, AccountType type);
    
    Optional<Account> findByAccountNumber(String accountNumber);
    
    Optional<Account> findByIban(String iban);
    
    boolean existsByAccountNumber(String accountNumber);
    
    boolean existsByIban(String iban);
    
    Optional<Account> findByAccountNumberAndCurrencyCode(String accountNumber, String currencyCode);
    
    Optional<Account> findByBlockchainAccountId(String blockchainAccountId);
    
    List<Account> findByType(AccountType type);
    
    List<Account> findByStatus(AccountStatus status);
    
    List<Account> findByStatusAndInterestRateNotNull(AccountStatus status);
}
