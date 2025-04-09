package com.ahmedyousri.boilerplate.springboot.banking.security.service;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.repository.AccountRepository;
import com.ahmedyousri.boilerplate.springboot.banking.audit.service.AuditService;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.repository.CustomerRepository;
import com.ahmedyousri.boilerplate.springboot.banking.exception.BusinessRuleException;
import com.ahmedyousri.boilerplate.springboot.banking.exception.ResourceNotFoundException;
import com.ahmedyousri.boilerplate.springboot.model.User;
import com.ahmedyousri.boilerplate.springboot.model.UserRole;
import com.ahmedyousri.boilerplate.springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for security-related operations.
 * This service provides methods for checking permissions, validating transaction limits, etc.
 */
@Service
@RequiredArgsConstructor
public class SecurityService {
    
    private static final Logger log = LoggerFactory.getLogger(SecurityService.class);
    
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final AuditService auditService;
    
    @Value("${app.security.transaction.daily-limit:10000}")
    private BigDecimal defaultDailyTransactionLimit;
    
    @Value("${app.security.transaction.monthly-limit:50000}")
    private BigDecimal defaultMonthlyTransactionLimit;
    
    // Cache for daily transaction amounts
    private final Map<String, Map<String, BigDecimal>> dailyTransactionAmounts = new ConcurrentHashMap<>();
    
    // Cache for monthly transaction amounts
    private final Map<String, Map<String, BigDecimal>> monthlyTransactionAmounts = new ConcurrentHashMap<>();
    
    /**
     * Validate that the user has permission to perform an operation on an account.
     *
     * @param username    The username of the user
     * @param accountId   The ID of the account
     * @param operation   The operation to perform
     * @throws BusinessRuleException If the user does not have permission to perform the operation
     */
    @Transactional(readOnly = true)
    public void validatePermission(String username, UUID accountId, String operation) {
        log.debug("Validating permission for user {} to perform {} on account {}", username, operation, accountId);
        
        // Get the user
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User", "username", username);
        }
        
        // Get the account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        
        // Check if the user is an admin
        if (user.getUserRole() == UserRole.ADMIN) {
            log.debug("User {} is an admin, permission granted", username);
            return;
        }
        
        // Check if the user is the owner of the account
        Customer customer = account.getCustomer();
        if (customer != null && customer.getUser() != null && customer.getUser().getUsername().equals(username)) {
            log.debug("User {} is the owner of account {}, permission granted", username, accountId);
            return;
        }
        
        // Check if the user has been granted specific permission for this account
        // This would typically involve checking a permissions table
        // For simplicity, we'll just check if the user is a regular user and the operation is read-only
        if (user.getUserRole() == UserRole.USER && isReadOnlyOperation(operation)) {
            log.debug("User {} is a regular user and operation {} is read-only, permission granted", username, operation);
            return;
        }
        
        // If we get here, the user does not have permission
        log.warn("User {} does not have permission to perform {} on account {}", username, operation, accountId);
        
        // Log the security violation
        auditService.logSystemOperation(
                "SECURITY_VIOLATION",
                "User " + username + " attempted to perform " + operation + " on account " + accountId + " without permission",
                username,
                false
        );
        
        throw new BusinessRuleException(
                "You do not have permission to perform this operation",
                "PERMISSION_DENIED"
        );
    }
    
    /**
     * Validate that a transaction does not exceed the user's transaction limits.
     *
     * @param username      The username of the user
     * @param amount        The amount of the transaction
     * @param operationType The type of operation
     * @throws BusinessRuleException If the transaction exceeds the user's transaction limits
     */
    @Transactional(readOnly = true)
    public void validateTransactionLimits(String username, BigDecimal amount, String operationType) {
        log.debug("Validating transaction limits for user {} to perform {} with amount {}", username, operationType, amount);
        
        // Get the user
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User", "username", username);
        }
        
        // Get the user's transaction limits
        BigDecimal dailyLimit = getDailyTransactionLimit(user);
        BigDecimal monthlyLimit = getMonthlyTransactionLimit(user);
        
        // Get the current date
        String today = LocalDateTime.now().toLocalDate().toString();
        String month = LocalDateTime.now().toLocalDate().toString().substring(0, 7);
        
        // Get the user's daily transaction amount
        Map<String, BigDecimal> userDailyAmounts = dailyTransactionAmounts.computeIfAbsent(username, k -> new HashMap<>());
        BigDecimal dailyAmount = userDailyAmounts.getOrDefault(today, BigDecimal.ZERO);
        
        // Get the user's monthly transaction amount
        Map<String, BigDecimal> userMonthlyAmounts = monthlyTransactionAmounts.computeIfAbsent(username, k -> new HashMap<>());
        BigDecimal monthlyAmount = userMonthlyAmounts.getOrDefault(month, BigDecimal.ZERO);
        
        // Check if the transaction would exceed the daily limit
        if (dailyAmount.add(amount).compareTo(dailyLimit) > 0) {
            log.warn("Transaction would exceed daily limit for user {}", username);
            
            // Log the security violation
            auditService.logFinancialOperation(
                    "TRANSACTION_LIMIT_EXCEEDED",
                    null,
                    amount,
                    "Transaction would exceed daily limit of " + dailyLimit,
                    username,
                    false
            );
            
            throw new BusinessRuleException(
                    "Transaction would exceed your daily limit of " + dailyLimit,
                    "DAILY_LIMIT_EXCEEDED"
            );
        }
        
        // Check if the transaction would exceed the monthly limit
        if (monthlyAmount.add(amount).compareTo(monthlyLimit) > 0) {
            log.warn("Transaction would exceed monthly limit for user {}", username);
            
            // Log the security violation
            auditService.logFinancialOperation(
                    "TRANSACTION_LIMIT_EXCEEDED",
                    null,
                    amount,
                    "Transaction would exceed monthly limit of " + monthlyLimit,
                    username,
                    false
            );
            
            throw new BusinessRuleException(
                    "Transaction would exceed your monthly limit of " + monthlyLimit,
                    "MONTHLY_LIMIT_EXCEEDED"
            );
        }
        
        // Update the user's daily transaction amount
        userDailyAmounts.put(today, dailyAmount.add(amount));
        
        // Update the user's monthly transaction amount
        userMonthlyAmounts.put(month, monthlyAmount.add(amount));
        
        log.debug("Transaction limits validated successfully for user {}", username);
    }
    
    /**
     * Get the current authenticated user.
     *
     * @return The current authenticated user
     * @throws BusinessRuleException If no user is authenticated
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessRuleException("No user is authenticated", "NOT_AUTHENTICATED");
        }
        
        return authentication.getName();
    }
    
    /**
     * Check if an operation is read-only.
     *
     * @param operation The operation to check
     * @return True if the operation is read-only, false otherwise
     */
    private boolean isReadOnlyOperation(String operation) {
        return operation.equals("VIEW") || operation.equals("LIST") || operation.equals("SEARCH");
    }
    
    /**
     * Get the daily transaction limit for a user.
     *
     * @param user The user
     * @return The daily transaction limit
     */
    private BigDecimal getDailyTransactionLimit(User user) {
        // In a real system, this would be retrieved from a database
        // For simplicity, we'll just return the default limit
        return defaultDailyTransactionLimit;
    }
    
    /**
     * Get the monthly transaction limit for a user.
     *
     * @param user The user
     * @return The monthly transaction limit
     */
    private BigDecimal getMonthlyTransactionLimit(User user) {
        // In a real system, this would be retrieved from a database
        // For simplicity, we'll just return the default limit
        return defaultMonthlyTransactionLimit;
    }
    
    /**
     * Reset the daily transaction amounts.
     * This method should be called at the end of each day.
     */
    public void resetDailyTransactionAmounts() {
        log.info("Resetting daily transaction amounts");
        dailyTransactionAmounts.clear();
    }
    
    /**
     * Reset the monthly transaction amounts.
     * This method should be called at the end of each month.
     */
    public void resetMonthlyTransactionAmounts() {
        log.info("Resetting monthly transaction amounts");
        monthlyTransactionAmounts.clear();
    }
}
