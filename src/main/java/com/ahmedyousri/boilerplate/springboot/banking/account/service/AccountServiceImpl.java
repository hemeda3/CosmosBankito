package com.ahmedyousri.boilerplate.springboot.banking.account.service;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountStatus;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.AccountType;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Statement;
import com.ahmedyousri.boilerplate.springboot.banking.account.mapper.AccountMapper;
import com.ahmedyousri.boilerplate.springboot.banking.account.repository.AccountRepository;
import com.ahmedyousri.boilerplate.springboot.banking.account.repository.StatementRepository;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.service.CurrentCustomerService;
import com.ahmedyousri.boilerplate.springboot.banking.exception.BusinessRuleException;
import com.ahmedyousri.boilerplate.springboot.banking.exception.InsufficientFundsException;
import com.ahmedyousri.boilerplate.springboot.banking.exception.ResourceNotFoundException;
import com.ahmedyousri.boilerplate.springboot.banking.util.MoneyUtil;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountBalanceResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountCloseRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountCreationRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountDetailResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountStatementsResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountUpdateRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.AccountsListResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.MessageResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.Money;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.MDC;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    
    
    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
    
    private final AccountRepository accountRepository;
    private final StatementRepository statementRepository;
    private final CurrentCustomerService currentCustomerService;
    private final AccountMapper accountMapper;
    private final com.ahmedyousri.boilerplate.springboot.banking.transaction.service.TransactionService transactionService;
    private final com.ahmedyousri.boilerplate.springboot.banking.accounting.service.AccountingService accountingService;
    private final com.ahmedyousri.boilerplate.springboot.banking.blockchain.service.BlockchainService blockchainService;
    private final com.ahmedyousri.boilerplate.springboot.banking.customer.repository.CustomerRepository customerRepository;
    
    /**
     * Perform a debit operation on an account (money leaving the account).
     * This method only modifies the account object but does not save it.
     * The calling method must save the account.
     * 
     * @param account The account to debit
     * @param amount The amount to debit
     * @throws BusinessRuleException if insufficient funds
     */
    public void performDebit(Account account, BigDecimal amount) {
        if (account.getAvailableBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    account.getId(),
                    amount,
                    account.getAvailableBalance());
        }
        
        account.setCurrentBalance(MoneyUtil.subtract(account.getCurrentBalance(), amount));
        account.setAvailableBalance(MoneyUtil.subtract(account.getAvailableBalance(), amount));
        account.setLastTransactionDate(LocalDateTime.now());
    }
    
    /**
     * Perform a credit operation on an account (money entering the account).
     * This method only modifies the account object but does not save it.
     * The calling method must save the account.
     * 
     * @param account The account to credit
     * @param amount The amount to credit
     */
    public void performCredit(Account account, BigDecimal amount) {
        account.setCurrentBalance(MoneyUtil.add(account.getCurrentBalance(), amount));
        account.setAvailableBalance(MoneyUtil.add(account.getAvailableBalance(), amount));
        account.setLastTransactionDate(LocalDateTime.now());
    }
    
    @Override
    @Transactional(readOnly = true)
    public AccountsListResponse getCustomerAccounts(String status, String type) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        List<Account> accounts;
        
        if (status != null && type != null) {
            AccountStatus accountStatus = AccountStatus.fromValue(status);
            AccountType accountType = AccountType.fromValue(type);
            accounts = accountRepository.findByCustomerAndStatusAndType(customer, accountStatus, accountType);
        } else if (status != null) {
            AccountStatus accountStatus = AccountStatus.fromValue(status);
            accounts = accountRepository.findByCustomerAndStatus(customer, accountStatus);
        } else if (type != null) {
            AccountType accountType = AccountType.fromValue(type);
            accounts = accountRepository.findByCustomerAndType(customer, accountType);
        } else {
            accounts = accountRepository.findByCustomer(customer);
        }
        
        List<AccountResponse> accountResponses = accounts.stream()
                .map(accountMapper::toAccountResponse)
                .collect(Collectors.toList());
        
        AccountsListResponse response = new AccountsListResponse();
        response.setAccounts(accountResponses);
        return response;
    }
    
    @Override
    @Transactional
    public AccountResponse createAccount(AccountCreationRequest request) {
        // Get the current customer - this should return a managed entity
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        // Ensure the customer is persisted if it's a new entity
        if (customer.getId() == null) {
            log.info("Customer is transient, saving it first");
            customer = customerRepository.save(customer);
        }
        
        Account account = accountMapper.toAccount(request);
        // Don't set ID - let Hibernate generate it with @GeneratedValue
        account.setAccountNumber(generateAccountNumber());
        account.setCustomer(customer);
        
        // Save the account first to get an ID
        Account savedAccount = accountRepository.save(account);
        
        // Set the blockchain account ID
        savedAccount.setBlockchainAccountId("mock_" + savedAccount.getId().toString());
        savedAccount = accountRepository.save(savedAccount);
        
        log.info("Created new account: {} for customer: {} with blockchain ID: {}", 
                savedAccount.getId(), customer.getId(), savedAccount.getBlockchainAccountId());
        
        return accountMapper.toAccountResponse(savedAccount);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AccountDetailResponse getAccountDetails(UUID accountId) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        Account account = findAccountAndVerifyOwnership(accountId, customer);
        
        return accountMapper.toAccountDetailResponse(account);
    }
    
    @Override
    @Transactional
    public AccountResponse updateAccountSettings(UUID accountId, AccountUpdateRequest request) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        Account account = findAccountAndVerifyOwnership(accountId, customer);
        
        if (request.getAccountName() != null) {
            account.setAccountName(request.getAccountName());
        }
        
        // Add more fields to update as needed
        
        Account updatedAccount = accountRepository.save(account);
        log.info("Updated account settings for account: {}", accountId);
        
        return accountMapper.toAccountResponse(updatedAccount);
    }
    
    @Override
    @Transactional
    public MessageResponse closeAccount(UUID accountId, AccountCloseRequest request) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        Account account = findAccountAndVerifyOwnership(accountId, customer);
        
        // Check if account can be closed
        if (!BigDecimal.ZERO.equals(account.getCurrentBalance())) {
            throw new BusinessRuleException(
                    "Account must have zero balance to close", 
                    "ACCOUNT_BALANCE_NOT_ZERO");
        }
        
        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        account.setClosureReason(request.getReason().getValue());
        
        accountRepository.save(account);
        log.info("Closed account: {}", accountId);
        
        MessageResponse response = new MessageResponse();
        response.setMessage("Account closed successfully");
        return response;
    }
    
    @Override
    @Transactional(readOnly = true)
    public AccountBalanceResponse getAccountBalance(UUID accountId) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        Account account = findAccountAndVerifyOwnership(accountId, customer);
        
        // Get balance from blockchain if account has a blockchain ID
        BigDecimal currentBalance = account.getCurrentBalance();
        BigDecimal availableBalance = account.getAvailableBalance();
        
        if (account.getBlockchainAccountId() != null) {
            try {
                com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.AccountBalance blockchainBalance =
                    blockchainService.getAccountBalance(accountId);
                
                if (blockchainBalance != null) {
                    currentBalance = blockchainBalance.getAmount();
                    availableBalance = blockchainBalance.getAmount(); // In blockchain, current and available are the same
                    
                    // Update the account with the blockchain balance
                    account.setCurrentBalance(currentBalance);
                    account.setAvailableBalance(availableBalance);
                    accountRepository.save(account);
                }
            } catch (Exception e) {
                log.warn("Failed to get balance from blockchain for account {}: {}", accountId, e.getMessage());
                // Fall back to database balance
            }
        }
        
        AccountBalanceResponse response = new AccountBalanceResponse();
        
        Money balance = new Money();
        balance.setAmount(currentBalance.toString());
        balance.setCurrencyCode(account.getCurrencyCode());
        response.setCurrentBalance(balance);
        
        Money availableMoney = new Money();
        availableMoney.setAmount(availableBalance.toString());
        availableMoney.setCurrencyCode(account.getCurrencyCode());
        response.setAvailableBalance(availableMoney);
        
        response.setTimestamp(account.getUpdatedAt() != null ? 
                toOffsetDateTime(account.getUpdatedAt()) : toOffsetDateTime(account.getCreatedAt()));
        
        return response;
    }
    
    @Override
    @Transactional(readOnly = true)
    public AccountStatementsResponse getAccountStatements(UUID accountId, LocalDate startDate, LocalDate endDate) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        Account account = findAccountAndVerifyOwnership(accountId, customer);
        
        List<Statement> statements;
        
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            statements = statementRepository.findByAccountAndGeneratedAtBetweenOrderByGeneratedAtDesc(
                    account, startDateTime, endDateTime);
        } else {
            statements = statementRepository.findByAccountOrderByGeneratedAtDesc(account);
        }
        
        AccountStatementsResponse response = new AccountStatementsResponse();
        response.setStatements(accountMapper.toStatementResponseList(statements));
        return response;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Resource downloadAccountStatement(UUID accountId, UUID statementId) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        Account account = findAccountAndVerifyOwnership(accountId, customer);
        
        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new ResourceNotFoundException("Statement", "id", statementId));
        
        if (!statement.getAccount().getId().equals(account.getId())) {
            throw new BusinessRuleException(
                    "Statement does not belong to the specified account", 
                    "STATEMENT_ACCOUNT_MISMATCH");
        }
        
        // In a real application, this would retrieve the actual statement file
        // For this example, we'll just return a placeholder
        String content = "This is a placeholder for account statement " + statementId + 
                " for account " + accountId;
        
        statement.setDownloadedAt(LocalDateTime.now());
        statementRepository.save(statement);
        
        return new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Find an account by ID and verify that it belongs to the specified customer
     */
    private Account findAccountAndVerifyOwnership(UUID accountId, Customer customer) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        
        if (!account.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessRuleException(
                    "Account does not belong to the authenticated customer", 
                    "ACCOUNT_OWNERSHIP_VIOLATION");
        }
        
        return account;
    }
    
    /**
     * Generate a unique account number
     */
    private String generateAccountNumber() {
        // In a real application, this would follow a specific format and validation
        return "ACCT" + System.currentTimeMillis();
    }
    
    /**
     * Convert LocalDateTime to OffsetDateTime
     */
    private OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }
    
    /**
     * Deposits money into a customer's account.
     * <p>
     * This method performs the following operations in a single transaction:
     * 1. Validates the account ownership and amount
     * 2. Updates the account balance
     * 3. Creates accounting entries (double-entry)
     * 4. Records a transaction for the customer's view
     * 5. Saves the updated account
     *
     * @param accountId   The ID of the account to deposit into
     * @param amount      The amount to deposit (must be positive)
     * @param description Optional description of the deposit
     * @return Updated account balance information
     * @throws ResourceNotFoundException If the account does not exist
     * @throws BusinessRuleException    If the amount is invalid or the account is not owned by the customer
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AccountBalanceResponse deposit(UUID accountId, BigDecimal amount, String description) {
        MDC.put("operation", "deposit");
        MDC.put("accountId", accountId.toString());
        
        long startTime = System.currentTimeMillis();
        try {
            Customer customer = currentCustomerService.getCurrentCustomer();
            Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
            
            if (!account.getCustomer().getId().equals(customer.getId())) {
                throw new BusinessRuleException(
                        "Account does not belong to the authenticated customer", 
                        "ACCOUNT_OWNERSHIP_VIOLATION");
            }
            
            // Validate amount
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessRuleException("Deposit amount must be greater than zero", "INVALID_DEPOSIT_AMOUNT");
            }
            
            // Generate a unique reference ID for this deposit
            UUID referenceId = UUID.randomUUID();
            
            // Create and execute blockchain command
            com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.DepositCommand command =
                new com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.DepositCommand(
                    accountId,
                    amount,
                    description != null ? description : "Deposit",
                    account.getCurrencyCode()
                );
            
            com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.BlockchainResponse response =
                blockchainService.executeCommand(command);
            
            if (!response.isSuccessful()) {
                throw new BusinessRuleException(
                    "Deposit failed on blockchain: " + response.getErrorMessage(),
                    response.getErrorCode()
                );
            }
            
            // Update blockchain account ID if not already set
            if (account.getBlockchainAccountId() == null) {
                account.setBlockchainAccountId("mock_" + accountId.toString());
                accountRepository.save(account);
            }
            
            // Perform operations in a specific order to maintain consistency
            // 1. Update account balance
            performCredit(account, amount);
            
            // 2. Create accounting entry (double-entry accounting)
            accountingService.createDepositJournalEntry(account, amount, 
                    description != null ? description : "Deposit", referenceId);
            
            // 3. Record customer-visible transaction
            transactionService.recordTransaction(account, 
                    com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.TransactionType.CREDIT,
                    amount, description != null ? description : "Deposit", referenceId.toString());
            
            // 4. Save the updated account
            accountRepository.save(account);
            
            log.info("Deposited {} {} to account {} (Blockchain TX: {})", 
                    amount, account.getCurrencyCode(), accountId, response.getTransactionHash());
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("Deposit operation completed in {} ms", duration);
            return getAccountBalance(accountId);
        } catch (Exception e) {
            log.error("Deposit failed: {}", e.getMessage(), e);
            throw e; // Let @Transactional handle the rollback
        } finally {
            MDC.remove("operation");
            MDC.remove("accountId");
        }
    }
    
    /**
     * Withdraws money from a customer's account.
     * <p>
     * This method performs the following operations in a single transaction:
     * 1. Validates the account ownership and amount
     * 2. Checks for sufficient funds
     * 3. Updates the account balance
     * 4. Creates accounting entries (double-entry)
     * 5. Records a transaction for the customer's view
     * 6. Saves the updated account
     *
     * @param accountId   The ID of the account to withdraw from
     * @param amount      The amount to withdraw (must be positive)
     * @param description Optional description of the withdrawal
     * @return Updated account balance information
     * @throws ResourceNotFoundException If the account does not exist
     * @throws BusinessRuleException    If the amount is invalid, the account is not owned by the customer,
     *                                  or there are insufficient funds
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AccountBalanceResponse withdraw(UUID accountId, BigDecimal amount, String description) {
        MDC.put("operation", "withdraw");
        MDC.put("accountId", accountId.toString());
        
        long startTime = System.currentTimeMillis();
        try {
            Customer customer = currentCustomerService.getCurrentCustomer();
            Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
            
            if (!account.getCustomer().getId().equals(customer.getId())) {
                throw new BusinessRuleException(
                        "Account does not belong to the authenticated customer", 
                        "ACCOUNT_OWNERSHIP_VIOLATION");
            }
            
            // Validate amount
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessRuleException("Withdrawal amount must be greater than zero", "INVALID_WITHDRAWAL_AMOUNT");
            }
            
            // Check if account has sufficient funds
            if (account.getAvailableBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException(
                        account.getId(),
                        amount,
                        account.getAvailableBalance());
            }
            
            // Update blockchain account ID if not already set
            if (account.getBlockchainAccountId() == null) {
                account.setBlockchainAccountId("mock_" + accountId.toString());
                accountRepository.save(account);
            }
            
            // Create and execute blockchain command
            com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.WithdrawCommand command =
                new com.ahmedyousri.boilerplate.springboot.banking.blockchain.command.WithdrawCommand(
                    accountId,
                    amount,
                    description != null ? description : "Withdrawal"
                );
            
            com.ahmedyousri.boilerplate.springboot.banking.blockchain.model.BlockchainResponse response =
                blockchainService.executeCommand(command);
            
            if (!response.isSuccessful()) {
                throw new BusinessRuleException(
                    "Withdrawal failed on blockchain: " + response.getErrorMessage(),
                    response.getErrorCode()
                );
            }
            
            // Generate a unique reference ID for this withdrawal
            UUID referenceId = UUID.randomUUID();
            
            // Perform operations in a specific order to maintain consistency
            // 1. Update account balance
            performDebit(account, amount);
            
            // 2. Create accounting entry (double-entry accounting)
            accountingService.createWithdrawalJournalEntry(
                    account, 
                    amount, 
                    description != null ? description : "Withdrawal",
                    referenceId
            );
            
            // 3. Record customer-visible transaction
            transactionService.recordTransaction(
                    account,
                    com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.TransactionType.DEBIT,
                    amount,
                    description != null ? description : "Withdrawal",
                    referenceId.toString()
            );
            
            // 4. Save the updated account
            accountRepository.save(account);
            
            log.info("Withdrew {} {} from account {} (Blockchain TX: {})", 
                    amount, account.getCurrencyCode(), accountId, response.getTransactionHash());
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("Withdrawal operation completed in {} ms", duration);
            return getAccountBalance(accountId);
        } catch (Exception e) {
            log.error("Withdrawal failed: {}", e.getMessage(), e);
            throw e; // Let @Transactional handle the rollback
        } finally {
            MDC.remove("operation");
            MDC.remove("accountId");
        }
    }
    
    /**
     * Generate a monthly statement for an account.
     * This method creates a statement record for the account for the previous month.
     *
     * @param accountId ID of the account
     */
    @Override
    @Transactional
    public void generateMonthlyStatement(UUID accountId) {
        log.info("Generating monthly statement for account: {}", accountId);
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        
        // Determine the statement period (previous month)
        LocalDate now = LocalDate.now();
        LocalDate previousMonth = now.minusMonths(1);
        String period = previousMonth.getYear() + "-" + String.format("%02d", previousMonth.getMonthValue());
        
        // Define the date range for the statement
        LocalDateTime startDate = previousMonth.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDate = previousMonth.withDayOfMonth(previousMonth.lengthOfMonth()).atTime(LocalTime.MAX);
        
        // Get transactions for the period
        List<com.ahmedyousri.boilerplate.springboot.banking.transaction.entity.Transaction> transactions =
                transactionService.getAccountTransactionsByDateRange(accountId, startDate.toLocalDate(), endDate.toLocalDate());
        
        // Calculate opening and closing balances
        BigDecimal openingBalance = BigDecimal.ZERO; // In a real app, this would be calculated from previous statement
        BigDecimal closingBalance = account.getCurrentBalance(); // In a real app, this would be calculated for the period
        
        // Generate a URL for the statement (in a real app, this would be a file path or S3 URL)
        String statementUrl = "/statements/" + accountId + "/" + period + ".pdf";
        
        // Create and save the statement record
        Statement statement = Statement.builder()
                .account(account)
                .period(period)
                .url(statementUrl)
                .generatedAt(LocalDateTime.now())
                .startDate(startDate)
                .endDate(endDate)
                .openingBalance(openingBalance)
                .closingBalance(closingBalance)
                .transactionCount(transactions.size())
                .build();
        
        statementRepository.save(statement);
        
        log.info("Generated monthly statement for account {}: period={}, transactions={}", 
                accountId, period, transactions.size());
    }
}
