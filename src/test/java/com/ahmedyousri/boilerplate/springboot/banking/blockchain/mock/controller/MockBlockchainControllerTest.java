//package com.ahmedyousri.boilerplate.springboot.banking.blockchain.mock.controller;
//
//import command.blockchain.banking.com.ahmedyousri.boilerplate.springboot.DepositCommand;
//import model.blockchain.banking.com.ahmedyousri.boilerplate.springboot.AccountBalance;
//import model.blockchain.banking.com.ahmedyousri.boilerplate.springboot.BlockchainResponse;
//import com.ahmedyousri.boilerplate.springboot.banking.blockchain.mock.service.MockBlockchainService;
//import com.ahmedyousri.boilerplate.springboot.banking.blockchain.mock.service.MockBlockchainState;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
///**
// * Tests for the MockBlockchainController.
// */
//public class MockBlockchainControllerTest {
//
//    @Mock
//    private MockBlockchainState mockBlockchainState;
//
//    @Mock
//    private MockBlockchainService mockBlockchainService;
//
//    @InjectMocks
//    private MockBlockchainController mockBlockchainController;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testDeposit() {
//        // Arrange
//        UUID accountId = UUID.randomUUID();
//        BigDecimal amount = new BigDecimal("100.00");
//        String description = "Test deposit";
//        String currencyCode = "USD";
//
//        BlockchainResponse expectedResponse = BlockchainResponse.builder()
//                .successful(true)
//                .transactionHash("mock-tx-hash-123")
//                .errorMessage("Deposit successful")
//                .build();
//
//        when(mockBlockchainService.executeCommand(any(DepositCommand.class))).thenReturn(expectedResponse);
//
//        // Act
//        ResponseEntity<BlockchainResponse> responseEntity = mockBlockchainController.deposit(
//                accountId.toString(), amount.toString(), description, currencyCode);
//
//        // Assert
//        assertNotNull(responseEntity);
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertNotNull(responseEntity.getBody());
//        assertEquals(true, responseEntity.getBody().isSuccessful());
//        assertEquals("mock-tx-hash-123", responseEntity.getBody().getTransactionHash());
//        assertEquals("Deposit successful", responseEntity.getBody().getErrorMessage());
//    }
//
//    @Test
//    public void testGetAccountBalance() {
//        // Arrange
//        UUID accountId = UUID.randomUUID();
//        AccountBalance expectedBalance = AccountBalance.builder()
//                .accountId(accountId)
//                .balance(new BigDecimal("500.00"))
//                .currencyCode("USD")
//                .blockNumber(12345L)
//                .build();
//
//        when(mockBlockchainService.getAccountBalance(accountId)).thenReturn(expectedBalance);
//
//        // Act
//        ResponseEntity<AccountBalance> responseEntity = mockBlockchainController.getAccountBalance(accountId.toString());
//
//        // Assert
//        assertNotNull(responseEntity);
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//        assertNotNull(responseEntity.getBody());
//        assertEquals(accountId, responseEntity.getBody().getAccountId());
//        assertEquals(new BigDecimal("500.00"), responseEntity.getBody().getBalance());
//        assertEquals("USD", responseEntity.getBody().getCurrencyCode());
//        assertEquals(12345L, responseEntity.getBody().getBlockNumber());
//    }
//}
