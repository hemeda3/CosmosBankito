package com.ahmedyousri.boilerplate.springboot.banking.account.integration;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountStatus;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountType;
import com.ahmedyousri.boilerplate.springboot.banking.blockchain.service.BlockchainService;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.service.CurrentCustomerService;
import com.ahmedyousri.boilerplate.springboot.banking.system.service.SystemAccountService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public BlockchainService mockBlockchainService() {
        return Mockito.mock(BlockchainService.class);
    }

    @Bean
    @Primary
    public CurrentCustomerService mockCurrentCustomerService() {
        return Mockito.mock(CurrentCustomerService.class);
    }
    
    @Bean
    @Primary
    public SystemAccountService mockSystemAccountService(
            com.ahmedyousri.boilerplate.springboot.banking.customer.repository.CustomerRepository customerRepository,
            com.ahmedyousri.boilerplate.springboot.banking.account.repository.AccountRepository accountRepository) {
        
        SystemAccountService mockService = Mockito.mock(SystemAccountService.class);
        
        // Create and save a mock system customer
        Customer systemCustomer = Customer.builder()
                .firstName("System")
                .lastName("System")
                .email("system@bank.com")
                .build();
        
        systemCustomer = customerRepository.save(systemCustomer);
        
        // Create and save a mock cash account
        Account cashAccount = Account.builder()
                .accountNumber("SYS-CASH-USD")
                .accountName("System Cash Account - USD")
                .type(AccountType.SYSTEM_ACCOUNT)
                .status(AccountStatus.ACTIVE)
                .currencyCode("USD")
                .currentBalance(BigDecimal.ZERO)
                .availableBalance(BigDecimal.ZERO)
                .openedDate(LocalDateTime.now())
                .customer(systemCustomer)
                .build();
        
        cashAccount = accountRepository.save(cashAccount);
        
        // Mock the getCashAccount method
        when(mockService.getCashAccount(anyString())).thenReturn(cashAccount);
        
        // Mock the getSystemCustomer method
        when(mockService.getSystemCustomer()).thenReturn(systemCustomer);
        
        return mockService;
    }
}
