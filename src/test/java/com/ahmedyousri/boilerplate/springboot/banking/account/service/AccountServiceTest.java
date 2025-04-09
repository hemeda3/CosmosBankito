package com.ahmedyousri.boilerplate.springboot.banking.account.service;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountStatus;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountType;
import com.ahmedyousri.boilerplate.springboot.banking.account.mapper.AccountMapper;
import com.ahmedyousri.boilerplate.springboot.banking.account.repository.AccountRepository;
import com.ahmedyousri.boilerplate.springboot.banking.account.repository.StatementRepository;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.service.AccountingService;
import com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.BlockchainResponse;
import com.ahmedyousri.boilerplate.springboot.banking.blockchain.service.BlockchainService;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.service.CurrentCustomerService;
import com.ahmedyousri.boilerplate.springboot.banking.exception.InsufficientFundsException;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.service.TransactionService;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountBalanceResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountCreationRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private CurrentCustomerService currentCustomerService;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountingService accountingService;

    @Mock
    private BlockchainService blockchainService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Customer testCustomer;
    private Account testAccount;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        
        testCustomer = Customer.builder()
                .id(UUID.randomUUID())
                .firstName("Test")
                .lastName("Customer")
                .email("test@example.com")
                .build();

        testAccount = Account.builder()
                .id(accountId)
                .accountNumber("TEST123456")
                .accountName("Test Account")
                .type(AccountType.CHECKING_ACCOUNT)
                .currencyCode("USD")
                .status(AccountStatus.ACTIVE)
                .currentBalance(new BigDecimal("100.00"))
                .availableBalance(new BigDecimal("100.00"))
                .openedDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .customer(testCustomer)
                .build();
    }

    @Test
    void testPerformDebit_Success() {
        // Arrange
        BigDecimal amount = new BigDecimal("50.00");
        
        // Act
        accountService.performDebit(testAccount, amount);
        
        // Assert
        assertEquals(0, new BigDecimal("50.00").compareTo(testAccount.getCurrentBalance()));
        assertEquals(0, new BigDecimal("50.00").compareTo(testAccount.getAvailableBalance()));
        assertNotNull(testAccount.getLastTransactionDate());
    }

    @Test
    void testPerformDebit_InsufficientFunds() {
        // Arrange
        BigDecimal amount = new BigDecimal("150.00");
        
        // Act & Assert
        assertThrows(InsufficientFundsException.class, () -> {
            accountService.performDebit(testAccount, amount);
        });
    }

    @Test
    void testPerformCredit_Success() {
        // Arrange
        BigDecimal amount = new BigDecimal("50.00");
        
        // Act
        accountService.performCredit(testAccount, amount);
        
        // Assert
        assertEquals(0, new BigDecimal("150.00").compareTo(testAccount.getCurrentBalance()));
        assertEquals(0, new BigDecimal("150.00").compareTo(testAccount.getAvailableBalance()));
        assertNotNull(testAccount.getLastTransactionDate());
    }

    @Test
    void testCreateAccount_Success() {
        // Arrange
        AccountCreationRequest request = new AccountCreationRequest();
        request.setAccountName("New Test Account");
        request.setType(AccountCreationRequest.TypeEnum.fromValue("currentAccount"));
        request.setCurrencyCode("USD");
        
        Account newAccount = Account.builder()
                .accountName("New Test Account")
                .type(AccountType.CHECKING_ACCOUNT)
                .currencyCode("USD")
                .status(AccountStatus.ACTIVE)
                .currentBalance(BigDecimal.ZERO)
                .availableBalance(BigDecimal.ZERO)
                .build();
        
        Account savedAccount = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber("TEST123456")
                .accountName("New Test Account")
                .type(AccountType.CHECKING_ACCOUNT)
                .currencyCode("USD")
                .status(AccountStatus.ACTIVE)
                .currentBalance(BigDecimal.ZERO)
                .availableBalance(BigDecimal.ZERO)
                .openedDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .customer(testCustomer)
                .build();
        
        AccountResponse expectedResponse = new AccountResponse();
        expectedResponse.setId(savedAccount.getId());
        expectedResponse.setAccountName("New Test Account");
        expectedResponse.setType(AccountResponse.TypeEnum.fromValue("currentAccount"));
        expectedResponse.setCurrencyCode("USD");
        expectedResponse.setStatus(AccountResponse.StatusEnum.fromValue("active"));
        
        when(currentCustomerService.getCurrentCustomer()).thenReturn(testCustomer);
        when(accountMapper.toAccount(request)).thenReturn(newAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        when(accountMapper.toAccountResponse(savedAccount)).thenReturn(expectedResponse);
        
        // Act
        AccountResponse response = accountService.createAccount(request);
        
        // Assert
        assertNotNull(response);
        assertEquals(savedAccount.getId(), response.getId());
        assertEquals("New Test Account", response.getAccountName());
        assertEquals(AccountResponse.TypeEnum.fromValue("currentAccount"), response.getType());
        assertEquals("USD", response.getCurrencyCode());
        assertEquals(AccountResponse.StatusEnum.fromValue("active"), response.getStatus());
        
        // Verify repository interactions
        verify(accountRepository, times(2)).save(any(Account.class));
    }

    @Test
    void testDeposit_Success() {
        // Arrange
        BigDecimal amount = new BigDecimal("50.00");
        String description = "Test deposit";
        
        when(currentCustomerService.getCurrentCustomer()).thenReturn(testCustomer);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));
        when(blockchainService.executeCommand(any())).thenReturn(
                BlockchainResponse.builder()
                        .successful(true)
                        .transactionHash("mock-tx-hash")
                        .blockHash("mock-block-hash")
                        .blockNumber(1L)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
        
        // Mock the getAccountBalance method to return a response with the updated balance
        com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.AccountBalance blockchainBalance =
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.AccountBalance.builder()
                        .accountId(accountId)
                        .balance(new BigDecimal("150.00"))
                        .currencyCode("USD")
                        .blockNumber(1L)
                        .timestamp(LocalDateTime.now())
                        .build();
        
        when(blockchainService.getAccountBalance(accountId)).thenReturn(blockchainBalance);
        
        // Act
        AccountBalanceResponse response = accountService.deposit(accountId, amount, description);
        
        // Assert
        assertNotNull(response);
        
        // Verify interactions
        verify(accountRepository, times(2)).findById(accountId);
        verify(blockchainService).executeCommand(any());
        verify(accountingService).createDepositJournalEntry(eq(testAccount), eq(amount), eq(description), any(UUID.class));
        verify(transactionService).recordTransaction(
                eq(testAccount), 
                eq(com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.TransactionType.CREDIT),
                eq(amount), 
                eq(description), 
                any(String.class)
        );
        verify(accountRepository, times(3)).save(testAccount);
    }

    @Test
    void testWithdraw_Success() {
        // Arrange
        BigDecimal amount = new BigDecimal("50.00");
        String description = "Test withdrawal";
        
        when(currentCustomerService.getCurrentCustomer()).thenReturn(testCustomer);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));
        when(blockchainService.executeCommand(any())).thenReturn(
                BlockchainResponse.builder()
                        .successful(true)
                        .transactionHash("mock-tx-hash")
                        .blockHash("mock-block-hash")
                        .blockNumber(1L)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
        
        // Mock the getAccountBalance method to return a response with the updated balance
        com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.AccountBalance blockchainBalance =
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.AccountBalance.builder()
                        .accountId(accountId)
                        .balance(new BigDecimal("50.00"))
                        .currencyCode("USD")
                        .blockNumber(1L)
                        .timestamp(LocalDateTime.now())
                        .build();
        
        when(blockchainService.getAccountBalance(accountId)).thenReturn(blockchainBalance);
        
        // Act
        AccountBalanceResponse response = accountService.withdraw(accountId, amount, description);
        
        // Assert
        assertNotNull(response);
        
        // Verify interactions
        verify(accountRepository, times(2)).findById(accountId);
        verify(blockchainService).executeCommand(any());
        verify(accountingService).createWithdrawalJournalEntry(eq(testAccount), eq(amount), eq(description), any(UUID.class));
        verify(transactionService).recordTransaction(
                eq(testAccount), 
                eq(com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.TransactionType.DEBIT),
                eq(amount), 
                eq(description), 
                any(String.class)
        );
        verify(accountRepository, times(3)).save(testAccount);
    }

    @Test
    void testWithdraw_InsufficientFunds() {
        // Arrange
        BigDecimal amount = new BigDecimal("150.00");
        String description = "Test withdrawal";
        
        when(currentCustomerService.getCurrentCustomer()).thenReturn(testCustomer);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(testAccount));
        
        // Act & Assert
        assertThrows(InsufficientFundsException.class, () -> {
            accountService.withdraw(accountId, amount, description);
        });
        
        // Verify no blockchain interaction happened
        verify(blockchainService, never()).executeCommand(any());
    }
}
