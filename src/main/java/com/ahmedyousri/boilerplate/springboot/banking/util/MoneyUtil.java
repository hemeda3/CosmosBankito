package com.ahmedyousri.boilerplate.springboot.banking.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class for money operations.
 * This class provides methods for consistent handling of money calculations.
 */
public class MoneyUtil {
    
    /**
     * The default scale for money calculations.
     */
    public static final int DEFAULT_SCALE = 4;
    
    /**
     * The default rounding mode for money calculations.
     */
    public static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;
    
    /**
     * Round a money amount to the default scale.
     *
     * @param amount The amount to round
     * @return The rounded amount
     */
    public static BigDecimal round(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
        }
        return amount.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
    }
    
    /**
     * Add two money amounts.
     *
     * @param a The first amount
     * @param b The second amount
     * @return The sum of the two amounts
     */
    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        if (a == null) {
            a = BigDecimal.ZERO;
        }
        if (b == null) {
            b = BigDecimal.ZERO;
        }
        return round(a.add(b));
    }
    
    /**
     * Subtract one money amount from another.
     *
     * @param a The amount to subtract from
     * @param b The amount to subtract
     * @return The difference between the two amounts
     */
    public static BigDecimal subtract(BigDecimal a, BigDecimal b) {
        if (a == null) {
            a = BigDecimal.ZERO;
        }
        if (b == null) {
            b = BigDecimal.ZERO;
        }
        return round(a.subtract(b));
    }
    
    /**
     * Multiply a money amount by a factor.
     *
     * @param amount The amount to multiply
     * @param factor The factor to multiply by
     * @return The product of the amount and the factor
     */
    public static BigDecimal multiply(BigDecimal amount, BigDecimal factor) {
        if (amount == null || factor == null) {
            return BigDecimal.ZERO.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
        }
        return round(amount.multiply(factor));
    }
    
    /**
     * Divide a money amount by a divisor.
     *
     * @param amount  The amount to divide
     * @param divisor The divisor to divide by
     * @return The quotient of the amount and the divisor
     * @throws ArithmeticException If the divisor is zero
     */
    public static BigDecimal divide(BigDecimal amount, BigDecimal divisor) {
        if (amount == null) {
            return BigDecimal.ZERO.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
        }
        if (divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return amount.divide(divisor, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }
    
    /**
     * Check if a money amount is positive.
     *
     * @param amount The amount to check
     * @return True if the amount is positive, false otherwise
     */
    public static boolean isPositive(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Check if a money amount is negative.
     *
     * @param amount The amount to check
     * @return True if the amount is negative, false otherwise
     */
    public static boolean isNegative(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) < 0;
    }
    
    /**
     * Check if a money amount is zero.
     *
     * @param amount The amount to check
     * @return True if the amount is zero, false otherwise
     */
    public static boolean isZero(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) == 0;
    }
    
    /**
     * Get the absolute value of a money amount.
     *
     * @param amount The amount to get the absolute value of
     * @return The absolute value of the amount
     */
    public static BigDecimal abs(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
        }
        return round(amount.abs());
    }
    
    /**
     * Get the maximum of two money amounts.
     *
     * @param a The first amount
     * @param b The second amount
     * @return The maximum of the two amounts
     */
    public static BigDecimal max(BigDecimal a, BigDecimal b) {
        if (a == null) {
            return round(b);
        }
        if (b == null) {
            return round(a);
        }
        return round(a.max(b));
    }
    
    /**
     * Get the minimum of two money amounts.
     *
     * @param a The first amount
     * @param b The second amount
     * @return The minimum of the two amounts
     */
    public static BigDecimal min(BigDecimal a, BigDecimal b) {
        if (a == null) {
            return round(b);
        }
        if (b == null) {
            return round(a);
        }
        return round(a.min(b));
    }
    
    /**
     * Format a money amount as a string.
     *
     * @param amount The amount to format
     * @return The formatted amount
     */
    public static String format(BigDecimal amount) {
        return round(amount).toPlainString();
    }
    
    /**
     * Parse a string as a money amount.
     *
     * @param amount The string to parse
     * @return The parsed amount
     * @throws NumberFormatException If the string cannot be parsed as a money amount
     */
    public static BigDecimal parse(String amount) {
        if (amount == null || amount.isEmpty()) {
            return BigDecimal.ZERO.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
        }
        return round(new BigDecimal(amount));
    }
}
