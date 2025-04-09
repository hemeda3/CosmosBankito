/*
package com.ahmedyousri.boilerplate.springboot.banking.blockchain.mock.service;

import command.blockchain.banking.com.ahmedyousri.boilerplate.springboot.DepositCommand;
import model.mock.blockchain.banking.com.ahmedyousri.boilerplate.springboot.MockAccount;
import model.mock.blockchain.banking.com.ahmedyousri.boilerplate.springboot.MockBlock;
import model.mock.blockchain.banking.com.ahmedyousri.boilerplate.springboot.MockTransaction;
import model.blockchain.banking.com.ahmedyousri.boilerplate.springboot.AccountBalance;
import model.blockchain.banking.com.ahmedyousri.boilerplate.springboot.BlockchainResponse;
import model.blockchain.banking.com.ahmedyousri.boilerplate.springboot.TransactionRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

*/
/**
 * Tests for the MockBlockchainService.
 *//*

public class MockBlockchainServiceTest {

    @Mock
    private MockBlockchainState mockBlockchainState;

    @InjectMocks
    private MockBlockchainService mockBlockchainService;

    private UUID accountId;
    private BigDecimal amount;
    private String description;
    private String currencyCode;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        accountId = UUID.randomUUID();
        amount = new BigDecimal("100.00");
        description = "Test deposit";
        currencyCode = "USD";
    }

    @Test
    public void testExecuteDepositCommand() {
        // Arrange
        DepositCommand depositCommand = new DepositCommand(accountId, amount, description, currencyCode);
        
        // Mock the behavior of the blockchain state
        MockAccount mockAccount = MockAccount.builder()
                .id(accountId)
                .balance(amount)
                .currencyCode(currencyCode)
                .build();
        
        // Mock the behavior of the blockchain state
        when(mockBlockchainState.deposit(eq(accountId), eq(amount), eq(description), eq(currencyCode)))
            .thenReturn("mock-tx-hash-123");
        
        // Mock the transaction
        MockTransaction mockTransaction = mock(MockTransaction.class);
        when(mockTransaction.getHash()).thenReturn("mock-tx-hash-123");
        when(mockTransaction.getBlockNumber()).thenReturn(1L);
        when(mockTransaction.getGasUsed()).thenReturn(21000L);
        
        // Mock the block
        MockBlock mockBlock = mock(MockBlock.class);
        when(mockBlock.getHash()).thenReturn("mock-block-hash-123");
        when(mockBlock.getNumber()).thenReturn(1L);
        
        when(mockBlockchainState.getTransactionByHash("mock-tx-hash-123")).thenReturn(mockTransaction);
        when(mockBlockchainState.getBlockByNumber(1L)).thenReturn(mockBlock);

        // Act
        BlockchainResponse response = mockBlockchainService.executeCommand(depositCommand);

        // Assert
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertEquals("mock-tx-hash-123", response.getTransactionHash());
        
        // Verify that the deposit method was called with the correct parameters
        verify(mockBlockchainState).deposit(accountId, amount, description, currencyCode);
    }

    @Test
    public void testGetAccountBalance() {
        // Arrange
        MockAccount mockAccount = MockAccount.builder()
                .id(accountId)
                .balance(new BigDecimal("500.00"))
                .currencyCode("USD")
                .build();
        
        when(mockBlockchainState.getAccountById(accountId)).thenReturn(mockAccount);
        when(mockBlockchainState.getCurrentBlockNumber()).thenReturn(12345L);
        
        // Act
        AccountBalance actualBalance = mockBlockchainService.getAccountBalance(accountId);

        // Assert
        assertNotNull(actualBalance);
        assertEquals(accountId, actualBalance.getAccountId());
        assertEquals(new BigDecimal("500.00"), actualBalance.getBalance());
        assertEquals("USD", actualBalance.getCurrencyCode());
        assertEquals(12345L, actualBalance.getBlockNumber());
        
        // Verify that the getAccountById method was called with the correct parameters
        verify(mockBlockchainState).getAccountById(accountId);
    }

    @Test
    public void testGetTransaction() {
        // Arrange
        String transactionHash = "mock-tx-hash-123";
        UUID fromAccountId = UUID.randomUUID();
        UUID toAccountId = UUID.randomUUID();
        
        // Create mock transaction
        MockTransaction mockTransaction = MockTransaction.builder()
                .hash(transactionHash)
                .from("0xsender")
                .to("0xrecipient")
                .amount(amount)
                .description(description)
                .currencyCode(currencyCode)
                .blockNumber(12345L)
                .build();
        
        // Create mock accounts
        MockAccount fromAccount = MockAccount.builder()
                .id(fromAccountId)
                .address("0xsender")
                .build();
        
        MockAccount toAccount = MockAccount.builder()
                .id(toAccountId)
                .address("0xrecipient")
                .build();
        
        when(mockBlockchainState.getTransactionByHash(transactionHash)).thenReturn(mockTransaction);
        when(mockBlockchainState.getAccountByAddress("0xsender")).thenReturn(fromAccount);
        when(mockBlockchainState.getAccountByAddress("0xrecipient")).thenReturn(toAccount);

        // Act
        TransactionRecord actualTransaction = mockBlockchainService.getTransaction(transactionHash);

        // Assert
        assertNotNull(actualTransaction);
        assertEquals(transactionHash, actualTransaction.getTransactionHash());
        assertEquals(fromAccountId, actualTransaction.getFromAccount());
        assertEquals(toAccountId, actualTransaction.getToAccount());
        assertEquals(amount, actualTransaction.getAmount());
        assertEquals(description, actualTransaction.getDescription());
        assertEquals(currencyCode, actualTransaction.getCurrencyCode());
        
        // Verify that the getTransactionByHash method was called with the correct parameters
        verify(mockBlockchainState).getTransactionByHash(transactionHash);
    }

    @Test
    public void testGetTransactions() {
        // Arrange
        TransactionRecord transaction1 = new TransactionRecord();
        transaction1.setTransactionHash("mock-tx-hash-1");
        transaction1.setFromAccount(accountId);
        transaction1.setAmount(new BigDecimal("100.00"));
        
        TransactionRecord transaction2 = new TransactionRecord();
        transaction2.setTransactionHash("mock-tx-hash-2");
        transaction2.setFromAccount(accountId);
        transaction2.setAmount(new BigDecimal("200.00"));
        
        List<TransactionRecord> expectedTransactions = List.of(transaction1, transaction2);
        
        when(mockBlockchainState.getTransactions(accountId)).thenReturn(expectedTransactions);

        // Act
        List<TransactionRecord> actualTransactions = mockBlockchainService.getTransactions(accountId);

        // Assert
        assertNotNull(actualTransactions);
        assertEquals(2, actualTransactions.size());
        assertEquals("mock-tx-hash-1", actualTransactions.get(0).getTransactionHash());
        assertEquals("mock-tx-hash-2", actualTransactions.get(1).getTransactionHash());
        
        // Verify that the getTransactions method was called with the correct parameters
        verify(mockBlockchainState).getTransactions(accountId);
    }
}
*/
