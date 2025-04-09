package com.ahmedyousri.boilerplate.springboot.banking.blockchain.cosmos;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountStatus;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountType;
import com.ahmedyousri.boilerplate.springboot.banking.account.repository.AccountRepository;
import com.ahmedyousri.boilerplate.springboot.banking.account.service.AccountService;
import com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.DepositCommand;
import com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.TransferCommand;
import com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.AccountBalance;
import com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.BlockchainResponse;
import com.ahmedyousri.boilerplate.springboot.banking.blockchain.service.BlockchainService;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.repository.CustomerRepository;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.entity.Transfer;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.repository.TransferRepository;
import com.ahmedyousri.boilerplate.springboot.banking.transfer.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for Cosmos Testnet blockchain integration.
 * This test demonstrates the end-to-end flow using a real testnet.
 * 
 * Test flow:
 * 1. Create a customer C1
 * 2. Create two accounts C1A and C1B for customer C1
 * 3. Deposit funds into account C1A
 * 4. Transfer funds from C1A to C1B
 * 5. Confirm all creations are done
 * 6. Confirm blockchain accounts vs database (H2) for testing
 * 7. Confirm transaction in testnet end-to-end flow
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CosmosTestnetIntegrationTest {

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransferRepository transferRepository;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private TransferService transferService;
    
    @Autowired
    private BlockchainService blockchainService;
    
    private Customer customer;
    private Account accountA;
    private Account accountB;
    
    @BeforeEach
    void setUp() {
        // Create a customer C1
        customer = new Customer();
        customer.setFirstName("Test");
        customer.setLastName("Customer");
        customer.setEmail("test.customer@example.com");
        customer.setPhoneNumber("+1234567890");
        customerRepository.save(customer);
        
        // Create account C1A
        accountA = new Account();
        accountA.setAccountNumber("TEST-A-" + UUID.randomUUID().toString().substring(0, 8));
        accountA.setAccountName("Test Account A");
        accountA.setType(AccountType.CHECKING_ACCOUNT);
        accountA.setStatus(AccountStatus.ACTIVE);
        accountA.setCurrencyCode("ATOM");
        accountA.setCurrentBalance(BigDecimal.ZERO);
        accountA.setAvailableBalance(BigDecimal.ZERO);
        accountA.setOpenedDate(LocalDateTime.now());
        accountA.setCustomer(customer);
        accountRepository.save(accountA);
        
        // Create account C1B
        accountB = new Account();
        accountB.setAccountNumber("TEST-B-" + UUID.randomUUID().toString().substring(0, 8));
        accountB.setAccountName("Test Account B");
        accountB.setType(AccountType.SAVINGS_ACCOUNT);
        accountB.setStatus(AccountStatus.ACTIVE);
        accountB.setCurrencyCode("ATOM");
        accountB.setCurrentBalance(BigDecimal.ZERO);
        accountB.setAvailableBalance(BigDecimal.ZERO);
        accountB.setOpenedDate(LocalDateTime.now());
        accountB.setCustomer(customer);
        accountRepository.save(accountB);
    }
    
    @Test
    void testEndToEndFlow() {
        // Step 1: Confirm customer and accounts are created
        assertNotNull(customer.getId());
        assertNotNull(accountA.getId());
        assertNotNull(accountB.getId());
        
        System.out.println("Customer created: " + customer.getId());
        System.out.println("Account A created: " + accountA.getId());
        System.out.println("Account B created: " + accountB.getId());
        
        // Step 2: Deposit funds into account C1A
        BigDecimal depositAmount = new BigDecimal("100.00");
        DepositCommand depositCommand = new DepositCommand(
                accountA.getId(),
                depositAmount,
                "Initial deposit",
                "ATOM"
        );
        
        BlockchainResponse depositResponse = blockchainService.executeCommand(depositCommand);
        assertTrue(depositResponse.isSuccessful(), "Deposit should be successful");
        assertNotNull(depositResponse.getTransactionHash(), "Transaction hash should not be null");
        
        System.out.println("Deposit transaction hash: " + depositResponse.getTransactionHash());
        
        // Step 3: Verify account balance on blockchain
        AccountBalance accountABalance = blockchainService.getAccountBalance(accountA.getId());
        assertNotNull(accountABalance);
        assertEquals(depositAmount, accountABalance.getBalance());
        assertEquals("ATOM", accountABalance.getCurrencyCode());
        
        System.out.println("Account A blockchain balance: " + accountABalance.getBalance() + " " + accountABalance.getCurrencyCode());
        System.out.println("Account A blockchain address: " + accountABalance.getAddress());
        
        // Step 4: Update database balance to match blockchain
        accountA.setCurrentBalance(depositAmount);
        accountA.setAvailableBalance(depositAmount);
        accountRepository.save(accountA);
        
        // Step 5: Transfer funds from C1A to C1B
        BigDecimal transferAmount = new BigDecimal("50.00");
        TransferCommand transferCommand = new TransferCommand();
        transferCommand.setFromAccountId(accountA.getId());
        transferCommand.setToAccountId(accountB.getId());
        transferCommand.setAmount(transferAmount);
        transferCommand.setDescription("Test transfer");
        transferCommand.setCurrencyCode("ATOM");
        
        BlockchainResponse transferResponse = blockchainService.executeCommand(transferCommand);
        assertTrue(transferResponse.isSuccessful(), "Transfer should be successful");
        assertNotNull(transferResponse.getTransactionHash(), "Transaction hash should not be null");
        
        System.out.println("Transfer transaction hash: " + transferResponse.getTransactionHash());
        
        // Step 6: Create a transfer record in the database
        Transfer transfer = new Transfer();
        transfer.setSourceAccount(accountA);
        transfer.setDestinationAccount(accountB);
        transfer.setAmount(transferAmount);
        transfer.setCurrencyCode("ATOM");
        transfer.setDescription("Test transfer");
        transfer.setStatus(Transfer.TransferStatus.COMPLETED);
        transfer.setReferenceId(transferResponse.getTransactionHash());
        transfer.setCreatedAt(LocalDateTime.now());
        transferRepository.save(transfer);
        
        // Step 7: Update account balances in the database
        accountA.setCurrentBalance(accountA.getCurrentBalance().subtract(transferAmount));
        accountA.setAvailableBalance(accountA.getAvailableBalance().subtract(transferAmount));
        accountRepository.save(accountA);
        
        accountB.setCurrentBalance(accountB.getCurrentBalance().add(transferAmount));
        accountB.setAvailableBalance(accountB.getAvailableBalance().add(transferAmount));
        accountRepository.save(accountB);
        
        // Step 8: Verify account balances on blockchain
        AccountBalance accountABalanceAfterTransfer = blockchainService.getAccountBalance(accountA.getId());
        AccountBalance accountBBalanceAfterTransfer = blockchainService.getAccountBalance(accountB.getId());
        
        assertNotNull(accountABalanceAfterTransfer);
        assertNotNull(accountBBalanceAfterTransfer);
        
        System.out.println("Account A blockchain balance after transfer: " + 
                accountABalanceAfterTransfer.getBalance() + " " + accountABalanceAfterTransfer.getCurrencyCode());
        System.out.println("Account B blockchain balance after transfer: " + 
                accountBBalanceAfterTransfer.getBalance() + " " + accountBBalanceAfterTransfer.getCurrencyCode());
        
        // Step 9: Verify database balances match blockchain balances
        assertEquals(accountABalanceAfterTransfer.getBalance(), accountA.getCurrentBalance());
        assertEquals(accountBBalanceAfterTransfer.getBalance(), accountB.getCurrentBalance());
        
        // Step 10: Verify transfer record
        Transfer savedTransfer = transferRepository.findByReferenceId(transferResponse.getTransactionHash()).orElse(null);
        assertNotNull(savedTransfer);
        assertEquals(Transfer.TransferStatus.COMPLETED, savedTransfer.getStatus());
        assertEquals(transferAmount, savedTransfer.getAmount());
        
        System.out.println("End-to-end test completed successfully");
    }
}
