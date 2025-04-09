//package com.ahmedyousri.boilerplate.springboot.banking.blockchain.integration;
//
//import command.blockchain.banking.com.ahmedyousri.boilerplate.springboot.DepositCommand;
//import cosmos.blockchain.banking.com.ahmedyousri.boilerplate.springboot.CosmosBlockchainService;
//import model.blockchain.banking.com.ahmedyousri.boilerplate.springboot.AccountBalance;
//import model.blockchain.banking.com.ahmedyousri.boilerplate.springboot.BlockchainResponse;
//import model.blockchain.banking.com.ahmedyousri.boilerplate.springboot.TransactionRecord;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//public class BlockchainIntegrationTest {
//
//    @Autowired
//    @Qualifier("cosmosTestnetBlockchainService")
//    private CosmosBlockchainService cosmosBlockchainService;
//
//    @BeforeEach
//    public void setup() {
//        // No reset needed for real blockchain service
//    }
//
//    @Test
//    public void testDepositFlow() {
//        // Arrange
//        UUID accountId = UUID.randomUUID();
//        BigDecimal amount = new BigDecimal("100.00");
//        String description = "Test deposit";
//        String currencyCode = "ATOM";
//
//        DepositCommand command = DepositCommand.builder()
//                .accountId(accountId)
//                .amount(amount)
//                .description(description)
//                .currencyCode(currencyCode)
//                .build();
//
//        // Act
//        BlockchainResponse response = cosmosBlockchainService.executeCommand(command);
//
//        // Assert
//        assertTrue(response.isSuccessful());
//        assertNotNull(response.getTransactionHash());
//        assertNotNull(response.getTimestamp());
//
//        // Verify account balance
//        AccountBalance balance = cosmosBlockchainService.getAccountBalance(accountId);
//        assertNotNull(balance);
//        assertEquals(0, amount.compareTo(balance.getBalance()));
//        assertEquals(currencyCode, balance.getCurrencyCode());
//        assertNotNull(balance.getAddress());
//    }
//
//    @Test
//    public void testMultipleDepositsFlow() {
//        // Arrange
//        UUID accountId = UUID.randomUUID();
//        BigDecimal amount1 = new BigDecimal("100.00");
//        BigDecimal amount2 = new BigDecimal("50.00");
//        String description = "Test deposit";
//        String currencyCode = "ATOM";
//
//        DepositCommand command1 = DepositCommand.builder()
//                .accountId(accountId)
//                .amount(amount1)
//                .description(description + " 1")
//                .currencyCode(currencyCode)
//                .build();
//
//        DepositCommand command2 = DepositCommand.builder()
//                .accountId(accountId)
//                .amount(amount2)
//                .description(description + " 2")
//                .currencyCode(currencyCode)
//                .build();
//
//        // Act
//        BlockchainResponse response1 = cosmosBlockchainService.executeCommand(command1);
//        BlockchainResponse response2 = cosmosBlockchainService.executeCommand(command2);
//
//        // Assert
//        assertTrue(response1.isSuccessful());
//        assertTrue(response2.isSuccessful());
//
//        // Verify account balance (should be the sum of both deposits)
//        AccountBalance balance = cosmosBlockchainService.getAccountBalance(accountId);
//        assertNotNull(balance);
//        assertEquals(0, amount1.add(amount2).compareTo(balance.getBalance()));
//        assertEquals(currencyCode, balance.getCurrencyCode());
//    }
//
//    @Test
//    public void testDepositAndGetBalanceFlow() {
//        // Arrange
//        UUID accountId = UUID.randomUUID();
//        BigDecimal amount = new BigDecimal("100.00");
//        String description = "Test deposit";
//        String currencyCode = "ATOM";
//
//        DepositCommand command = DepositCommand.builder()
//                .accountId(accountId)
//                .amount(amount)
//                .description(description)
//                .currencyCode(currencyCode)
//                .build();
//
//        // Act
//        BlockchainResponse depositResponse = cosmosBlockchainService.executeCommand(command);
//        AccountBalance balance = cosmosBlockchainService.getAccountBalance(accountId);
//
//        // Assert
//        assertTrue(depositResponse.isSuccessful());
//        assertNotNull(balance);
//        assertEquals(0, amount.compareTo(balance.getBalance()));
//        assertEquals(currencyCode, balance.getCurrencyCode());
//        assertNotNull(balance.getAddress());
//    }
//
//    @Test
//    public void testDirectServiceCall() {
//        // Arrange
//        UUID accountId = UUID.randomUUID();
//        BigDecimal amount = new BigDecimal("100.00");
//        String description = "Test direct deposit";
//        String currencyCode = "ATOM";
//
//        // Act - Call the service method directly
//        String transactionHash = cosmosBlockchainService.deposit(accountId, amount, description, currencyCode);
//
//        // Assert
//        assertNotNull(transactionHash);
//
//        // Verify transaction
//        TransactionRecord transaction = cosmosBlockchainService.getTransaction(transactionHash);
//        assertNotNull(transaction);
//        assertEquals(transactionHash, transaction.getHash());
//        assertEquals(0, amount.compareTo(transaction.getAmount()));
//        assertEquals(currencyCode, transaction.getCurrencyCode());
//        assertEquals(description, transaction.getDescription());
//        assertEquals("COMPLETED", transaction.getStatus());
//
//        // Verify account balance
//        AccountBalance balance = cosmosBlockchainService.getAccountBalance(accountId);
//        assertNotNull(balance);
//        assertEquals(0, amount.compareTo(balance.getBalance()));
//    }
//}
