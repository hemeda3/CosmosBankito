package com.ahmedyousri.boilerplate.springboot.banking.account.service;

import com.ahmedyousri.boilerplate.springboot.model.generated.AccountBalanceResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountCloseRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountCreationRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountDetailResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountStatementsResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountUpdateRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountsListResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.MessageResponse;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.util.UUID;

import java.math.BigDecimal;

public interface AccountService {
    
    /**
     * Get all accounts for the authenticated customer
     * 
     * @param status Optional filter by account status
     * @param type Optional filter by account type
     * @return List of accounts
     */
    AccountsListResponse getCustomerAccounts(String status, String type);
    
    /**
     * Create a new account for the authenticated customer
     * 
     * @param request Account creation details
     * @return Created account details
     */
    AccountResponse createAccount(AccountCreationRequest request);
    
    /**
     * Get detailed information about a specific account
     * 
     * @param accountId ID of the account
     * @return Account details
     */
    AccountDetailResponse getAccountDetails(UUID accountId);
    
    /**
     * Update account settings
     * 
     * @param accountId ID of the account
     * @param request Account update details
     * @return Updated account details
     */
    AccountResponse updateAccountSettings(UUID accountId, AccountUpdateRequest request);
    
    /**
     * Close an account
     * 
     * @param accountId ID of the account
     * @param request Account closure details
     * @return Closure confirmation message
     */
    MessageResponse closeAccount(UUID accountId, AccountCloseRequest request);
    
    /**
     * Get account balance
     * 
     * @param accountId ID of the account
     * @return Account balance information
     */
    AccountBalanceResponse getAccountBalance(UUID accountId);
    
    /**
     * Get account statements
     * 
     * @param accountId ID of the account
     * @param startDate Optional start date for filtering statements
     * @param endDate Optional end date for filtering statements
     * @return List of account statements
     */
    AccountStatementsResponse getAccountStatements(UUID accountId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Download a specific account statement
     * 
     * @param accountId ID of the account
     * @param statementId ID of the statement
     * @return Statement file as a resource
     */
    Resource downloadAccountStatement(UUID accountId, UUID statementId);
    
    /**
     * Deposit money into an account
     * 
     * @param accountId ID of the account
     * @param amount Amount to deposit
     * @param description Optional description of the deposit
     * @return Updated account balance
     */
    AccountBalanceResponse deposit(UUID accountId, BigDecimal amount, String description);
    
    /**
     * Withdraw money from an account
     * 
     * @param accountId ID of the account
     * @param amount Amount to withdraw
     * @param description Optional description of the withdrawal
     * @return Updated account balance
     */
    AccountBalanceResponse withdraw(UUID accountId, BigDecimal amount, String description);
    
    /**
     * Generate a monthly statement for an account
     * 
     * @param accountId ID of the account
     */
    void generateMonthlyStatement(UUID accountId);
    
    /**
     * Perform a credit operation on an account
     * 
     * @param account The account to credit
     * @param amount The amount to credit
     */
    void performCredit(com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account account, BigDecimal amount);
    
    /**
     * Perform a debit operation on an account
     * 
     * @param account The account to debit
     * @param amount The amount to debit
     */
    void performDebit(com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account account, BigDecimal amount);
}
