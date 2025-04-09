package com.ahmedyousri.boilerplate.springboot.banking.blockchain.command;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the DepositCommand class.
 */
public class DepositCommandTest {

    @Test
    public void testDepositCommandCreation() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
        String description = "Test deposit";
        String currencyCode = "USD";

        // Act
        DepositCommand depositCommand = new DepositCommand(accountId, amount, description, currencyCode);

        // Assert
        assertEquals(accountId, depositCommand.getAccountId());
        assertEquals(amount, depositCommand.getAmount());
        assertEquals(description, depositCommand.getDescription());
        assertEquals(currencyCode, depositCommand.getCurrencyCode());
        assertEquals(FinancialCommand.CommandType.DEPOSIT, depositCommand.getType());
    }

    @Test
    public void testDepositCommandWithNullDescription() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
        String currencyCode = "USD";

        // Act
        DepositCommand depositCommand = new DepositCommand(accountId, amount, null, currencyCode);

        // Assert
        assertEquals(accountId, depositCommand.getAccountId());
        assertEquals(amount, depositCommand.getAmount());
        assertNull(depositCommand.getDescription());
        assertEquals(currencyCode, depositCommand.getCurrencyCode());
        assertEquals(FinancialCommand.CommandType.DEPOSIT, depositCommand.getType());
    }

    @Test
    public void testDepositCommandWithDefaultCurrencyCode() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
        String description = "Test deposit";
        String currencyCode = "USD"; // Default currency code

        // Act
        DepositCommand depositCommand = new DepositCommand(accountId, amount, description, currencyCode);

        // Assert
        assertEquals(accountId, depositCommand.getAccountId());
        assertEquals(amount, depositCommand.getAmount());
        assertEquals(description, depositCommand.getDescription());
        assertEquals("USD", depositCommand.getCurrencyCode()); // Default currency code should be USD
        assertEquals(FinancialCommand.CommandType.DEPOSIT, depositCommand.getType());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
        String description = "Test deposit";
        String currencyCode = "USD";

        DepositCommand command1 = new DepositCommand(accountId, amount, description, currencyCode);
        DepositCommand command2 = new DepositCommand(accountId, amount, description, currencyCode);
        DepositCommand command3 = new DepositCommand(UUID.randomUUID(), amount, description, currencyCode);

        // Assert equals
        assertEquals(command1, command2);
        assertNotEquals(command1, command3);

        // Assert hashCode
        assertEquals(command1.hashCode(), command2.hashCode());
        assertNotEquals(command1.hashCode(), command3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
        String description = "Test deposit";
        String currencyCode = "USD";

        DepositCommand depositCommand = new DepositCommand(accountId, amount, description, currencyCode);

        // Act
        String toString = depositCommand.toString();

        // Assert
        assertTrue(toString.contains(accountId.toString()));
        assertTrue(toString.contains(amount.toString()));
        assertTrue(toString.contains(description));
        assertTrue(toString.contains(currencyCode));
    }

    @Test
    public void testValidation() {
        // Test with null accountId
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new DepositCommand(null, new BigDecimal("100.00"), "Test deposit", "USD");
        });
        assertTrue(exception.getMessage().contains("accountId cannot be null"));

        // Test with null amount
        exception = assertThrows(IllegalArgumentException.class, () -> {
            new DepositCommand(UUID.randomUUID(), null, "Test deposit", "USD");
        });
        assertTrue(exception.getMessage().contains("amount cannot be null"));

        // Test with negative amount
        exception = assertThrows(IllegalArgumentException.class, () -> {
            new DepositCommand(UUID.randomUUID(), new BigDecimal("-100.00"), "Test deposit", "USD");
        });
        assertTrue(exception.getMessage().contains("amount must be positive"));

        // Test with zero amount
        exception = assertThrows(IllegalArgumentException.class, () -> {
            new DepositCommand(UUID.randomUUID(), BigDecimal.ZERO, "Test deposit", "USD");
        });
        assertTrue(exception.getMessage().contains("amount must be positive"));

        // Test with null currencyCode
        exception = assertThrows(IllegalArgumentException.class, () -> {
            new DepositCommand(UUID.randomUUID(), new BigDecimal("100.00"), "Test deposit", null);
        });
        assertTrue(exception.getMessage().contains("currencyCode cannot be null or empty"));

        // Test with empty currencyCode
        exception = assertThrows(IllegalArgumentException.class, () -> {
            new DepositCommand(UUID.randomUUID(), new BigDecimal("100.00"), "Test deposit", "");
        });
        assertTrue(exception.getMessage().contains("currencyCode cannot be null or empty"));
    }
}
