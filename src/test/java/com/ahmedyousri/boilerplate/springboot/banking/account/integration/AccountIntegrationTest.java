package com.ahmedyousri.boilerplate.springboot.banking.account.integration;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountStatus;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountType;
import com.ahmedyousri.boilerplate.springboot.banking.account.repository.AccountRepository;
import com.ahmedyousri.boilerplate.springboot.banking.account.service.AccountService;
import com.ahmedyousri.boilerplate.springboot.banking.blockchain.service.BlockchainService;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.service.CurrentCustomerService;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountBalanceResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountCreationRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Integration test for the Account service layer.
 * This test verifies the end-to-end flow of account operations.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AccountIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private CurrentCustomerService currentCustomerService;

    @MockBean
    private BlockchainService blockchainService;

    private Customer testCustomer;
    private Account testAccount;

    @BeforeEach
    public void setup() {
        // Create a test customer
        testCustomer = Customer.builder()
                .firstName("Test")
                .lastName("Customer")
                .email("test@example.com")
                .build();

        // Mock the current customer service to return our test customer
        when(currentCustomerService.getCurrentCustomer()).thenReturn(testCustomer);

        // Mock blockchain responses
        when(blockchainService.executeCommand(any())).thenReturn(
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.BlockchainResponse.builder()
                        .successful(true)
                        .transactionHash("mock-tx-hash")
                        .blockHash("mock-block-hash")
                        .blockNumber(1L)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        // Create a test account - don't set ID manually
        testAccount = Account.builder()
                .version(0L) // Set initial version for optimistic locking
                .accountNumber("TEST123456")
                .accountName("Test Account")
                .type(AccountType.CHECKING_ACCOUNT)
                .currencyCode("USD")
                .status(AccountStatus.ACTIVE)
                .currentBalance(BigDecimal.ZERO)
                .availableBalance(BigDecimal.ZERO)
                .openedDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .customer(testCustomer)
                .build();
    }

    @Test
    public void testCreateAccount() {
        // Arrange
        AccountCreationRequest request = new AccountCreationRequest();
        request.setAccountName("New Test Account");
        request.setType(AccountCreationRequest.TypeEnum.fromValue("currentAccount"));
        request.setCurrencyCode("USD");

        // Act
        AccountResponse response = accountService.createAccount(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("New Test Account", response.getAccountName());
        assertEquals(AccountResponse.TypeEnum.fromValue("savingsAccount"), response.getType());
        assertEquals("USD", response.getCurrencyCode());
        assertEquals(AccountResponse.StatusEnum.fromValue("active"), response.getStatus());

        // Verify the account was saved to the database
        Optional<Account> savedAccount = accountRepository.findById(UUID.fromString(response.getId().toString()));
        assertTrue(savedAccount.isPresent());
        assertEquals("New Test Account", savedAccount.get().getAccountName());
        assertEquals(AccountType.CHECKING_ACCOUNT, savedAccount.get().getType());
        assertEquals("USD", savedAccount.get().getCurrencyCode());
        assertEquals(AccountStatus.ACTIVE, savedAccount.get().getStatus());
        assertEquals(BigDecimal.ZERO, savedAccount.get().getCurrentBalance());
        assertEquals(BigDecimal.ZERO, savedAccount.get().getAvailableBalance());
        assertNotNull(savedAccount.get().getBlockchainAccountId());
    }

    @Test
    public void testDepositAndWithdraw() {
        // Save the test account to the database
        Account savedAccount = accountRepository.save(testAccount);
        UUID accountId = savedAccount.getId();

        // Mock blockchain account balance
        when(blockchainService.getAccountBalance(any())).thenReturn(
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.AccountBalance.builder()
                        .accountId(accountId)
                        .balance(BigDecimal.ZERO)
                        .currencyCode("USD")
                        .blockNumber(0L)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        // Test deposit
        BigDecimal depositAmount = new BigDecimal("100.00");
        AccountBalanceResponse depositResponse = accountService.deposit(accountId, depositAmount, "Test deposit");

        // Verify deposit
        assertNotNull(depositResponse);
        assertEquals("100.00", depositResponse.getCurrentBalance().getAmount());
        assertEquals("100.00", depositResponse.getAvailableBalance().getAmount());
        assertEquals("USD", depositResponse.getCurrentBalance().getCurrencyCode());

        // Verify account in database after deposit
        Account accountAfterDeposit = accountRepository.findById(accountId).orElseThrow();
        assertEquals(0, new BigDecimal("100.00").compareTo(accountAfterDeposit.getCurrentBalance()));
        assertEquals(0, new BigDecimal("100.00").compareTo(accountAfterDeposit.getAvailableBalance()));

        // Test withdrawal - use the account ID, not the account object
        BigDecimal withdrawAmount = new BigDecimal("50.00");
        AccountBalanceResponse withdrawResponse = accountService.withdraw(accountId, withdrawAmount, "Test withdrawal");

        // Verify withdrawal
        assertNotNull(withdrawResponse);
        assertEquals("50.00", withdrawResponse.getCurrentBalance().getAmount());
        assertEquals("50.00", withdrawResponse.getAvailableBalance().getAmount());
        assertEquals("USD", withdrawResponse.getCurrentBalance().getCurrencyCode());

        // Verify account in database after withdrawal
        Account accountAfterWithdrawal = accountRepository.findById(accountId).orElseThrow();
        assertEquals(0, new BigDecimal("50.00").compareTo(accountAfterWithdrawal.getCurrentBalance()));
        assertEquals(0, new BigDecimal("50.00").compareTo(accountAfterWithdrawal.getAvailableBalance()));
    }

    @Test
    public void testGetAccountBalance() {
        // Save the test account with an initial balance
        testAccount.setCurrentBalance(new BigDecimal("200.00"));
        testAccount.setAvailableBalance(new BigDecimal("200.00"));
        Account savedAccount = accountRepository.save(testAccount);

        // Mock blockchain account balance
        when(blockchainService.getAccountBalance(savedAccount.getId())).thenReturn(
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.AccountBalance.builder()
                        .accountId(savedAccount.getId())
                        .balance(new BigDecimal("200.00"))
                        .currencyCode("USD")
                        .blockNumber(1L)
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        // Get account balance
        AccountBalanceResponse balanceResponse = accountService.getAccountBalance(savedAccount.getId());

        // Verify balance
        assertNotNull(balanceResponse);
        assertEquals("200.00", balanceResponse.getCurrentBalance().getAmount());
        assertEquals("200.00", balanceResponse.getAvailableBalance().getAmount());
        assertEquals("USD", balanceResponse.getCurrentBalance().getCurrencyCode());
    }
}
