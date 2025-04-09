package com.ahmedyousri.boilerplate.springboot.banking.system.service;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountStatus;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountType;
import com.ahmedyousri.boilerplate.springboot.banking.account.repository.AccountRepository;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.repository.CustomerRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing system accounts.
 * This service provides methods for retrieving and creating system accounts.
 */
@Service
@RequiredArgsConstructor
public class SystemAccountService {
    
    private static final Logger log = LoggerFactory.getLogger(SystemAccountService.class);
    
    private static final UUID SYSTEM_CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String SYSTEM_CUSTOMER_NAME = "System";
    
    private static final UUID CASH_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String CASH_ACCOUNT_NUMBER_PREFIX = "SYS-CASH-";
    private static final String CASH_ACCOUNT_NAME_PREFIX = "System Cash Account - ";
    
    private static final UUID CLEARING_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final String CLEARING_ACCOUNT_NUMBER_PREFIX = "SYS-CLEARING-";
    private static final String CLEARING_ACCOUNT_NAME_PREFIX = "System Clearing Account - ";
    
    private static final UUID SUSPENSE_ACCOUNT_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private static final String SUSPENSE_ACCOUNT_NUMBER_PREFIX = "SYS-SUSPENSE-";
    private static final String SUSPENSE_ACCOUNT_NAME_PREFIX = "System Suspense Account - ";
    
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    
    /**
     * Cache for system accounts to avoid repeated database lookups.
     */
    private final Map<String, Account> systemAccountCache = new ConcurrentHashMap<>();
    
    /**
     * Initialize system accounts.
     */
    @PostConstruct
    public void initializeSystemAccounts() {
        log.info("Initializing system accounts");
        
        try {
            // Ensure system customer exists
            ensureSystemCustomerExists();
            
            // Ensure system accounts exist for default currencies
            getCashAccount("USD");
            getCashAccount("EUR");
            getClearingAccount("USD");
            getClearingAccount("EUR");
            getSuspenseAccount("USD");
            getSuspenseAccount("EUR");
            
            log.info("System accounts initialized successfully");
        } catch (Exception e) {
            log.error("Error initializing system accounts: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Get the cash account for a currency.
     *
     * @param currencyCode The currency code
     * @return The cash account
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Account getCashAccount(String currencyCode) {
        String cacheKey = "CASH_" + currencyCode;
        
        // First check the cache
        Account cachedAccount = systemAccountCache.get(cacheKey);
        if (cachedAccount != null) {
            return cachedAccount;
        }
        
        // Try to find the cash account in the database
        Optional<Account> cashAccount = accountRepository.findByAccountNumberAndCurrencyCode(
                CASH_ACCOUNT_NUMBER_PREFIX + currencyCode, currencyCode);
        
        // If it exists, cache it and return
        if (cashAccount.isPresent()) {
            systemAccountCache.put(cacheKey, cashAccount.get());
            return cashAccount.get();
        }
        
        // If it doesn't exist, create it with synchronized block to prevent concurrent creation
        synchronized (this) {
            // Check again in case another thread created it while we were waiting
            cashAccount = accountRepository.findByAccountNumberAndCurrencyCode(
                    CASH_ACCOUNT_NUMBER_PREFIX + currencyCode, currencyCode);
            
            if (cashAccount.isPresent()) {
                systemAccountCache.put(cacheKey, cashAccount.get());
                return cashAccount.get();
            }
            
            // Create the account
            log.info("Creating system cash account for currency: {}", currencyCode);
            
            Account newCashAccount = Account.builder()
                    .accountNumber(CASH_ACCOUNT_NUMBER_PREFIX + currencyCode)
                    .accountName(CASH_ACCOUNT_NAME_PREFIX + currencyCode)
                    .type(AccountType.SYSTEM_ACCOUNT)
                    .status(AccountStatus.ACTIVE)
                    .currencyCode(currencyCode)
                    .currentBalance(BigDecimal.ZERO)
                    .availableBalance(BigDecimal.ZERO)
                    .openedDate(LocalDateTime.now())
                    .customer(getSystemCustomer())
                    .build();
            
            try {
                Account savedAccount = accountRepository.save(newCashAccount);
                systemAccountCache.put(cacheKey, savedAccount);
                return savedAccount;
            } catch (Exception e) {
                log.warn("Failed to create cash account: {}", e.getMessage());
                // If creation fails, try to find it one more time
                return accountRepository.findByAccountNumberAndCurrencyCode(
                        CASH_ACCOUNT_NUMBER_PREFIX + currencyCode, currencyCode)
                        .orElseThrow(() -> new RuntimeException("Failed to create or find cash account"));
            }
        }
    }
    
    /**
     * Get the clearing account for a currency.
     *
     * @param currencyCode The currency code
     * @return The clearing account
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Account getClearingAccount(String currencyCode) {
        String cacheKey = "CLEARING_" + currencyCode;
        
        // First check the cache
        Account cachedAccount = systemAccountCache.get(cacheKey);
        if (cachedAccount != null) {
            return cachedAccount;
        }
        
        // Try to find the clearing account in the database
        Optional<Account> clearingAccount = accountRepository.findByAccountNumberAndCurrencyCode(
                CLEARING_ACCOUNT_NUMBER_PREFIX + currencyCode, currencyCode);
        
        // If it exists, cache it and return
        if (clearingAccount.isPresent()) {
            systemAccountCache.put(cacheKey, clearingAccount.get());
            return clearingAccount.get();
        }
        
        // If it doesn't exist, create it with synchronized block to prevent concurrent creation
        synchronized (this) {
            // Check again in case another thread created it while we were waiting
            clearingAccount = accountRepository.findByAccountNumberAndCurrencyCode(
                    CLEARING_ACCOUNT_NUMBER_PREFIX + currencyCode, currencyCode);
            
            if (clearingAccount.isPresent()) {
                systemAccountCache.put(cacheKey, clearingAccount.get());
                return clearingAccount.get();
            }
            
            // Create the account
            log.info("Creating system clearing account for currency: {}", currencyCode);
            
            Account newClearingAccount = new Account();
            // Don't set ID - let Hibernate manage it
            newClearingAccount.setAccountNumber(CLEARING_ACCOUNT_NUMBER_PREFIX + currencyCode);
            newClearingAccount.setAccountName(CLEARING_ACCOUNT_NAME_PREFIX + currencyCode);
            newClearingAccount.setType(AccountType.SYSTEM_ACCOUNT);
            newClearingAccount.setStatus(AccountStatus.ACTIVE);
            newClearingAccount.setCurrencyCode(currencyCode);
            newClearingAccount.setCurrentBalance(BigDecimal.ZERO);
            newClearingAccount.setAvailableBalance(BigDecimal.ZERO);
            newClearingAccount.setOpenedDate(LocalDateTime.now());
            newClearingAccount.setCustomer(getSystemCustomer());
            
            try {
                Account savedAccount = accountRepository.save(newClearingAccount);
                systemAccountCache.put(cacheKey, savedAccount);
                return savedAccount;
            } catch (Exception e) {
                log.warn("Failed to create clearing account: {}", e.getMessage());
                // If creation fails, try to find it one more time
                return accountRepository.findByAccountNumberAndCurrencyCode(
                        CLEARING_ACCOUNT_NUMBER_PREFIX + currencyCode, currencyCode)
                        .orElseThrow(() -> new RuntimeException("Failed to create or find clearing account"));
            }
        }
    }
    
    /**
     * Get the suspense account for a currency.
     *
     * @param currencyCode The currency code
     * @return The suspense account
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Account getSuspenseAccount(String currencyCode) {
        String cacheKey = "SUSPENSE_" + currencyCode;
        
        // First check the cache
        Account cachedAccount = systemAccountCache.get(cacheKey);
        if (cachedAccount != null) {
            return cachedAccount;
        }
        
        // Try to find the suspense account in the database
        Optional<Account> suspenseAccount = accountRepository.findByAccountNumberAndCurrencyCode(
                SUSPENSE_ACCOUNT_NUMBER_PREFIX + currencyCode, currencyCode);
        
        // If it exists, cache it and return
        if (suspenseAccount.isPresent()) {
            systemAccountCache.put(cacheKey, suspenseAccount.get());
            return suspenseAccount.get();
        }
        
        // If it doesn't exist, create it with synchronized block to prevent concurrent creation
        synchronized (this) {
            // Check again in case another thread created it while we were waiting
            suspenseAccount = accountRepository.findByAccountNumberAndCurrencyCode(
                    SUSPENSE_ACCOUNT_NUMBER_PREFIX + currencyCode, currencyCode);
            
            if (suspenseAccount.isPresent()) {
                systemAccountCache.put(cacheKey, suspenseAccount.get());
                return suspenseAccount.get();
            }
            
            // Create the account
            log.info("Creating system suspense account for currency: {}", currencyCode);
            
            Account newSuspenseAccount = new Account();
            // Don't set ID - let Hibernate manage it
            newSuspenseAccount.setAccountNumber(SUSPENSE_ACCOUNT_NUMBER_PREFIX + currencyCode);
            newSuspenseAccount.setAccountName(SUSPENSE_ACCOUNT_NAME_PREFIX + currencyCode);
            newSuspenseAccount.setType(AccountType.SYSTEM_ACCOUNT);
            newSuspenseAccount.setStatus(AccountStatus.ACTIVE);
            newSuspenseAccount.setCurrencyCode(currencyCode);
            newSuspenseAccount.setCurrentBalance(BigDecimal.ZERO);
            newSuspenseAccount.setAvailableBalance(BigDecimal.ZERO);
            newSuspenseAccount.setOpenedDate(LocalDateTime.now());
            newSuspenseAccount.setCustomer(getSystemCustomer());
            
            try {
                Account savedAccount = accountRepository.save(newSuspenseAccount);
                systemAccountCache.put(cacheKey, savedAccount);
                return savedAccount;
            } catch (Exception e) {
                log.warn("Failed to create suspense account: {}", e.getMessage());
                // If creation fails, try to find it one more time
                return accountRepository.findByAccountNumberAndCurrencyCode(
                        SUSPENSE_ACCOUNT_NUMBER_PREFIX + currencyCode, currencyCode)
                        .orElseThrow(() -> new RuntimeException("Failed to create or find suspense account"));
            }
        }
    }
    
    /**
     * Get the system customer.
     *
     * @return The system customer
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Customer getSystemCustomer() {
        try {
            // Try to find the system customer by email
            Optional<Customer> existingCustomerByEmail = customerRepository.findByEmail("system@bank.com");
            if (existingCustomerByEmail.isPresent()) {
                return existingCustomerByEmail.get();
            }
            
            // If it doesn't exist, create it with synchronized block to prevent concurrent creation
            synchronized (this) {
                // Check again in case another thread created it while we were waiting
                existingCustomerByEmail = customerRepository.findByEmail("system@bank.com");
                if (existingCustomerByEmail.isPresent()) {
                    return existingCustomerByEmail.get();
                }
                
                log.info("Creating system customer");
                
                // Create a new system customer without setting ID
                Customer newSystemCustomer = new Customer();
                newSystemCustomer.setFirstName(SYSTEM_CUSTOMER_NAME);
                newSystemCustomer.setLastName(SYSTEM_CUSTOMER_NAME);
                newSystemCustomer.setEmail("system@bank.com");
                
                try {
                    return customerRepository.save(newSystemCustomer);
                } catch (Exception e) {
                    log.warn("Failed to save system customer: {}", e.getMessage());
                    
                    // If creation fails, try to find it one more time
                    return customerRepository.findByEmail("system@bank.com")
                            .orElseThrow(() -> new RuntimeException("Failed to create or find system customer"));
                }
            }
        } catch (Exception e) {
            log.error("Error getting system customer: {}", e.getMessage(), e);
            
            // Create a temporary system customer that's not persisted
            // This allows the application to continue running even if there's a database issue
            Customer tempSystemCustomer = new Customer();
            tempSystemCustomer.setFirstName(SYSTEM_CUSTOMER_NAME);
            tempSystemCustomer.setLastName(SYSTEM_CUSTOMER_NAME);
            tempSystemCustomer.setEmail("system@bank.com");
            
            return tempSystemCustomer;
        }
    }
    
    /**
     * Ensure the system customer exists.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void ensureSystemCustomerExists() {
        getSystemCustomer();
    }
    
    /**
     * Clear the system account cache.
     */
    public void clearCache() {
        systemAccountCache.clear();
    }
}
