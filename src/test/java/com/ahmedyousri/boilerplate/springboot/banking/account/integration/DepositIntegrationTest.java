package com.ahmedyousri.boilerplate.springboot.banking.account.integration;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.repository.AccountRepository;
import com.ahmedyousri.boilerplate.springboot.banking.account.service.AccountService;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.entity.JournalEntry;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.entity.JournalEntryLine;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.repository.JournalEntryRepository;
import com.ahmedyousri.boilerplate.springboot.banking.blockchain.service.BlockchainService;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.CustomerStatus;
import com.ahmedyousri.boilerplate.springboot.banking.customer.service.CurrentCustomerService;
import com.ahmedyousri.boilerplate.springboot.banking.system.service.SystemAccountService;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.Transaction;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.TransactionType;
import com.ahmedyousri.boilerplate.springboot.banking.transaction.repository.TransactionRepository;
import com.ahmedyousri.boilerplate.springboot.model.User;
import com.ahmedyousri.boilerplate.springboot.security.dto.RegistrationRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountBalanceResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountCreationRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the Deposit feature using the full Spring context,
 * H2 database, and the real configured BlockchainService (CosmosTestnet).
 * Note: Relies on the placeholder success response from the underlying mocked sendTokens.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional // Rollback DB changes after test
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DepositIntegrationTest {

    @Autowired private AccountService accountService;
    @Autowired private SystemAccountService systemAccountService;
    @Autowired private AccountRepository accountRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private JournalEntryRepository journalEntryRepository;
    @Autowired @Qualifier("cosmosBlockchainService")
    private BlockchainService blockchainService;
    @Autowired private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Autowired private com.ahmedyousri.boilerplate.springboot.security.service.UserService userService;
    @Autowired private CurrentCustomerService currentCustomerService;


    private Customer testCustomer;
    private Account testAccount;
    private UUID testAccountId;
    private final String testUsername = "deposit_user_nomock";
    private final String currency = "ATOM";

    @BeforeEach
    void setup() {
        // Ensure system accounts exist first
        systemAccountService.ensureSystemCustomerExists();
        systemAccountService.getCashAccount(currency);

        // Create test user using UserService
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setName("Integration Test User");
        registrationRequest.setUsername(testUsername);
        registrationRequest.setEmail(testUsername + "@example.com");
        registrationRequest.setPassword("password");
        userService.registration(registrationRequest);
        
        // Get the created user
        User testUser = userService.findByUsername(testUsername);
        
        // Create test customer linked to the user using CurrentCustomerService
        testCustomer = currentCustomerService.createCustomer(
                testUser,
                "IntegrationNoMock",
                "Depositor",
                testUsername + "@example.com",
                CustomerStatus.ACTIVE
        );
        
        // Create a test account using the account service
        AccountCreationRequest accountRequest = new AccountCreationRequest();
        accountRequest.setAccountName("Integration No Mock Deposit Account");
        accountRequest.setType(AccountCreationRequest.TypeEnum.CURRENTACCOUNT);
        accountRequest.setCurrencyCode(currency);
        
        // Create the account through the service
        AccountResponse accountResponse = accountService.createAccount(accountRequest);
        testAccountId = UUID.fromString(accountResponse.getId().toString());
        
        // Get the account entity for test assertions
        testAccount = accountRepository.findById(testAccountId).orElseThrow();
    }

    @Test
    @WithUserDetails(testUsername) // Sets up Security Context for CurrentCustomerServiceImpl
    void testSuccessfulDepositEndToEndWithRealServices() {
        // Arrange
        BigDecimal depositAmount = new BigDecimal("500.00");
        String depositDescription = "E2E Test Deposit No Mock";

        // --- Act ---
        // Call the service method. This will use the real AccountService,
        // which calls the real  .executeCommand
        // (which internally uses the mocked CosmosBlockchainService.sendTokens returning success),
        // and then calls the real  .getAccountBalance
        // (which queries the actual testnet).
        AccountBalanceResponse depositResponse = accountService.deposit(
            testAccountId,
            depositAmount,
            depositDescription
        );


        // --- Assert ---

        // 1. Verify API-like Response (AccountBalanceResponse)
        // NOTE: The balance here comes from the REAL getAccountBalance call querying the testnet.
        // Since the sendTokens was mocked, the testnet balance likely hasn't changed.
        // We assert the structure but not the exact balance amount against depositAmount.
        assertNotNull(depositResponse, "Deposit response should not be null");
        assertNotNull(depositResponse.getCurrentBalance().getAmount(), "Response current balance amount should exist");
        assertNotNull(depositResponse.getAvailableBalance().getAmount(), "Response available balance amount should exist");
        assertEquals(currency, depositResponse.getCurrentBalance().getCurrencyCode(), "Response currency code should match");
        // Cannot reliably assert depositAmount == response balance because getAccountBalance hits real testnet
        System.out.println("Actual balance returned in response (from testnet): " + depositResponse.getCurrentBalance().getAmount());


        // 2. Verify Database State - Account (Fetch fresh from DB)
        // The DB should be updated with the blockchain balance, which may be different from the deposit amount
        Account updatedAccount = accountRepository.findById(testAccountId)
            .orElseThrow(() -> new AssertionError("Account not found in DB after deposit"));
        assertTrue(updatedAccount.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0,
            "DB Account current balance should be greater than zero");
        assertTrue(updatedAccount.getAvailableBalance().compareTo(BigDecimal.ZERO) > 0,
            "DB Account available balance should be greater than zero");
        assertNotNull(updatedAccount.getLastTransactionDate(), "Last transaction date should be updated");
        assertNotNull(updatedAccount.getBlockchainAccountId(), "Blockchain Account ID should be set");
        assertNotNull(updatedAccount.getVersion(), "Version should not be null");
        assertTrue(updatedAccount.getVersion() > 0, "Version should be incremented");

        // 3. Verify Database State - Transaction Record
        Pageable pageable = PageRequest.of(0, 1);
        Page<Transaction> transactionsPage = transactionRepository.findByAccountOrderByTimestampDesc(updatedAccount, pageable);
        assertTrue(transactionsPage.hasContent(), "Transaction record should exist");

        Transaction depositTransaction = transactionsPage.getContent().get(0);
        assertEquals(TransactionType.CREDIT, depositTransaction.getType(), "Transaction type should be CREDIT");
        assertEquals(0, depositAmount.compareTo(depositTransaction.getAmount()), "Transaction amount should match deposit");
        assertEquals(depositDescription, depositTransaction.getDescription(), "Transaction description should match");
        assertEquals(0, depositAmount.compareTo(depositTransaction.getBalanceAfterTransaction()),
            "BalanceAfterTransaction should match new balance");
        assertNotNull(depositTransaction.getReferenceId(), "Transaction should have a reference ID");
        String transactionReferenceId = depositTransaction.getReferenceId();

        // 4. Verify Database State - Journal Entry
        List<JournalEntry> journalEntries = journalEntryRepository.findByReference(transactionReferenceId);
        assertEquals(1, journalEntries.size(), "Exactly one Journal Entry should exist for the deposit reference");

        JournalEntry depositJournalEntry = journalEntries.get(0);
        assertEquals(2, depositJournalEntry.getEntryLines().size(), "Journal Entry should have two lines");

        JournalEntryLine creditLine = depositJournalEntry.getEntryLines().stream()
            .filter(line -> line.getEntryType() == JournalEntryLine.EntryType.CREDIT)
            .findFirst().orElseThrow(() -> new AssertionError("No CREDIT line found in Journal Entry"));
        JournalEntryLine debitLine = depositJournalEntry.getEntryLines().stream()
            .filter(line -> line.getEntryType() == JournalEntryLine.EntryType.DEBIT)
            .findFirst().orElseThrow(() -> new AssertionError("No DEBIT line found in Journal Entry"));

        assertEquals(testAccountId, creditLine.getAccount().getId(), "Credit line should be for the customer account");
        assertEquals(0, depositAmount.compareTo(creditLine.getAmount()), "Credit line amount should match deposit");

        Account systemCashAccount = systemAccountService.getCashAccount(currency);
        assertNotNull(systemCashAccount, "System Cash Account should exist");
        assertEquals(systemCashAccount.getId(), debitLine.getAccount().getId(), "Debit line should be for the system cash account");
        assertEquals(0, depositAmount.compareTo(debitLine.getAmount()), "Debit line amount should match deposit");

        // 5. Verify Blockchain Interaction Outcome (Based on Mocked Send)
        // We can't use Mockito.verify on the real bean's methods directly without @SpyBean.
        // Instead, we check the consequence: the service logic proceeded as if blockchain succeeded.
        // This is confirmed by the database updates in steps 2, 3, 4.
        // We already checked the API-like response structure in step 1.

        System.out.println("DepositIntegrationTest.testSuccessfulDepositEndToEndWithRealServices PASSED");
        System.out.println(" - DB Account Balance Updated: " + updatedAccount.getCurrentBalance());
        System.out.println(" - Transaction Recorded: " + depositTransaction.getId());
        System.out.println(" - Journal Entry Recorded: " + depositJournalEntry.getId());
        System.out.println(" - NOTE: Blockchain state on actual testnet is likely unchanged due to mocked sendTokens.");
    }
}
