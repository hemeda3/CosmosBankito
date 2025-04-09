//package com.ahmedyousri.boilerplate.springboot.banking.blockchain.mock.service;
//
//import command.blockchain.banking.com.ahmedyousri.boilerplate.springboot.DepositCommand;
//import command.blockchain.banking.com.ahmedyousri.boilerplate.springboot.FinancialCommand;
//import command.blockchain.banking.com.ahmedyousri.boilerplate.springboot.TransferCommand;
//import command.blockchain.banking.com.ahmedyousri.boilerplate.springboot.WithdrawCommand;
//import config.blockchain.banking.com.ahmedyousri.boilerplate.springboot.BlockchainConfig;
//import model.mock.blockchain.banking.com.ahmedyousri.boilerplate.springboot.MockAccount;
//import model.mock.blockchain.banking.com.ahmedyousri.boilerplate.springboot.MockBlock;
//import model.mock.blockchain.banking.com.ahmedyousri.boilerplate.springboot.MockTransaction;
//import model.blockchain.banking.com.ahmedyousri.boilerplate.springboot.AccountBalance;
//import model.blockchain.banking.com.ahmedyousri.boilerplate.springboot.BlockchainResponse;
//import model.blockchain.banking.com.ahmedyousri.boilerplate.springboot.TransactionRecord;
//import service.blockchain.banking.com.ahmedyousri.boilerplate.springboot.BlockchainService;
//import util.banking.com.ahmedyousri.boilerplate.springboot.MoneyUtil;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//import java.util.stream.Collectors;
//
///**
// * Mock implementation of the BlockchainService interface.
// * This implementation simulates a blockchain for testing and development.
// */
////@Service
////@ConditionalOnProperty(name = "blockchain.mock.enabled", havingValue = "true")
//@RequiredArgsConstructor
//public class MockBlockchainService implements BlockchainService {
//
//    private static final Logger log = LoggerFactory.getLogger(MockBlockchainService.class);
//
//    private final MockBlockchainState blockchainState;
//    private final BlockchainConfig blockchainConfig;
//
//    private final Executor executor = Executors.newSingleThreadExecutor();
//
//    /**
//     * Initialize the mock blockchain service.
//     */
//    @PostConstruct
//    public void initialize() {
//        log.info("Initializing mock blockchain service");
//        blockchainState.initialize();
//        blockchainConfig.logConfig();
//    }
//
//    /**
//     * Ensure the blockchain state is initialized.
//     * This method checks if the blockchain state has been initialized and initializes it if not.
//     */
//    private void ensureInitialized() {
//        if (blockchainState.getGenesisBlock() == null || blockchainState.getLatestBlock() == null) {
//            blockchainState.initialize();
//        }
//    }
//
//    @Override
//    public BlockchainResponse executeCommand(FinancialCommand command) {
//        log.info("Executing command: {}", command.getClass().getSimpleName());
//
//        // Ensure the blockchain state is initialized
//        ensureInitialized();
//
//        try {
//            if (command instanceof DepositCommand) {
//                return executeDepositCommand((DepositCommand) command);
//            } else if (command instanceof WithdrawCommand) {
//                return executeWithdrawCommand((WithdrawCommand) command);
//            } else if (command instanceof TransferCommand) {
//                return executeTransferCommand((TransferCommand) command);
//            } else {
//                return BlockchainResponse.builder()
//                        .successful(false)
//                        .errorCode("UNSUPPORTED_COMMAND")
//                        .errorMessage("Unsupported command type: " + command.getClass().getSimpleName())
//                        .build();
//            }
//        } catch (Exception e) {
//            log.error("Error executing command: {}", e.getMessage(), e);
//            return BlockchainResponse.builder()
//                    .successful(false)
//                    .errorCode("COMMAND_EXECUTION_ERROR")
//                    .errorMessage(e.getMessage())
//                    .build();
//        }
//    }
//
//    @Override
//    public CompletableFuture<BlockchainResponse> executeCommandAsync(FinancialCommand command) {
//        return CompletableFuture.supplyAsync(() -> executeCommand(command), executor);
//    }
//
//    @Override
//    public AccountBalance getAccountBalance(UUID accountId) {
//        log.info("Getting account balance for account: {}", accountId);
//
//        // Ensure the blockchain state is initialized
//        ensureInitialized();
//
//        try {
//            MockAccount account = blockchainState.getAccountById(accountId);
//            if (account == null) {
//                // Create the account if it doesn't exist
//                account = blockchainState.createAccount(accountId, "USD");
//            }
//
//            return AccountBalance.builder()
//                    .accountId(accountId)
//                    .balance(account.getBalance())
//                    .currencyCode(account.getCurrencyCode())
//                    .address(account.getAddress())
//                    .blockNumber(blockchainState.getCurrentBlockNumber())
//                    .timestamp(LocalDateTime.now())
//                    .build();
//        } catch (Exception e) {
//            log.error("Error getting account balance: {}", e.getMessage(), e);
//            return AccountBalance.builder()
//                    .accountId(accountId)
//                    .balance(BigDecimal.ZERO)
//                    .currencyCode("USD")
//                    .blockNumber(blockchainState.getCurrentBlockNumber())
//                    .timestamp(LocalDateTime.now())
//                    .error(e.getMessage())
//                    .build();
//        }
//    }
//
//    @Override
//    public CompletableFuture<AccountBalance> getAccountBalanceAsync(UUID accountId) {
//        return CompletableFuture.supplyAsync(() -> getAccountBalance(accountId), executor);
//    }
//
//    @Override
//    public List<TransactionRecord> getTransactionHistory(UUID accountId, int limit) {
//        log.info("Getting transaction history for account: {}", accountId);
//
//        // Ensure the blockchain state is initialized
//        ensureInitialized();
//
//        try {
//            MockAccount account = blockchainState.getAccountById(accountId);
//            if (account == null) {
//                return new ArrayList<>();
//            }
//
//            List<MockTransaction> transactions = blockchainState.getTransactionHistory(accountId);
//
//            return transactions.stream()
//                    .limit(limit)
//                    .map(this::convertToTransactionRecord)
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            log.error("Error getting transaction history: {}", e.getMessage(), e);
//            return new ArrayList<>();
//        }
//    }
//
//    @Override
//    public CompletableFuture<List<TransactionRecord>> getTransactionHistoryAsync(UUID accountId, int limit) {
//        return CompletableFuture.supplyAsync(() -> getTransactionHistory(accountId, limit), executor);
//    }
//
//    @Override
//    public TransactionRecord getTransaction(String transactionHash) {
//        log.info("Getting transaction: {}", transactionHash);
//
//        // Ensure the blockchain state is initialized
//        ensureInitialized();
//
//        try {
//            MockTransaction transaction = blockchainState.getTransactionByHash(transactionHash);
//            if (transaction == null) {
//                return null;
//            }
//
//            return convertToTransactionRecord(transaction);
//        } catch (Exception e) {
//            log.error("Error getting transaction: {}", e.getMessage(), e);
//            return null;
//        }
//    }
//
//    @Override
//    public CompletableFuture<TransactionRecord> getTransactionAsync(String transactionHash) {
//        return CompletableFuture.supplyAsync(() -> getTransaction(transactionHash), executor);
//    }
//
//    /**
//     * Execute a deposit command.
//     *
//     * @param command The deposit command
//     * @return The blockchain response
//     */
//    private BlockchainResponse executeDepositCommand(DepositCommand command) {
//        log.info("Executing deposit command for account: {} amount: {}", command.getAccountId(), command.getAmount());
//
//        // Ensure the blockchain state is initialized
//        ensureInitialized();
//
//        try {
//            // Use the deposit method from blockchainState which handles all the logic
//            String transactionHash = blockchainState.deposit(
//                    command.getAccountId(),
//                    command.getAmount(),
//                    command.getDescription(),
//                    command.getCurrencyCode()
//            );
//
//            // Get the transaction
//            MockTransaction transaction = blockchainState.getTransactionByHash(transactionHash);
//            if (transaction == null) {
//                throw new IllegalStateException("Transaction not found after deposit");
//            }
//
//            // Get the block
//            MockBlock block = blockchainState.getBlockByNumber(transaction.getBlockNumber());
//
//            return BlockchainResponse.builder()
//                    .successful(true)
//                    .transactionHash(transaction.getHash())
//                    .blockHash(block != null ? block.getHash() : null)
//                    .blockNumber(block != null ? block.getNumber() : 0)
//                    .gasUsed(transaction.getGasUsed())
//                    .timestamp(LocalDateTime.now())
//                    .build();
//        } catch (Exception e) {
//            log.error("Error executing deposit command: {}", e.getMessage(), e);
//            return BlockchainResponse.builder()
//                    .successful(false)
//                    .errorCode("DEPOSIT_ERROR")
//                    .errorMessage(e.getMessage())
//                    .timestamp(LocalDateTime.now())
//                    .build();
//        }
//    }
//
//    /**
//     * Execute a withdraw command.
//     *
//     * @param command The withdraw command
//     * @return The blockchain response
//     */
//    private BlockchainResponse executeWithdrawCommand(WithdrawCommand command) {
//        log.info("Executing withdraw command for account: {} amount: {}", command.getAccountId(), command.getAmount());
//
//        try {
//            // Get the account
//            MockAccount account = blockchainState.getAccountById(command.getAccountId());
//            if (account == null) {
//                return BlockchainResponse.builder()
//                        .successful(false)
//                        .errorCode("ACCOUNT_NOT_FOUND")
//                        .errorMessage("Account not found: " + command.getAccountId())
//                        .timestamp(LocalDateTime.now())
//                        .build();
//            }
//
//            // Check if the account has sufficient funds
//            if (account.getBalance().compareTo(command.getAmount()) < 0) {
//                return BlockchainResponse.builder()
//                        .successful(false)
//                        .errorCode("INSUFFICIENT_FUNDS")
//                        .errorMessage("Insufficient funds in account: " + command.getAccountId())
//                        .timestamp(LocalDateTime.now())
//                        .build();
//            }
//
//            // Debit the account
//            account.debit(command.getAmount());
//
//            // Create a transaction from the account to the system account
//            UUID systemAccountId = UUID.fromString("00000000-0000-0000-0000-000000000001");
//            MockAccount systemAccount = blockchainState.getAccountById(systemAccountId);
//            if (systemAccount == null) {
//                systemAccount = blockchainState.createAccount(systemAccountId, "USD");
//            }
//
//            MockTransaction transaction = blockchainState.createTransaction(
//                    command.getAccountId(),
//                    systemAccountId,
//                    command.getAmount(),
//                    command.getDescription(),
//                    account.getCurrencyCode()
//            );
//
//            // Mine a block to confirm the transaction
//            MockBlock block = blockchainState.mineBlock();
//
//            return BlockchainResponse.builder()
//                    .successful(true)
//                    .transactionHash(transaction.getHash())
//                    .blockHash(block != null ? block.getHash() : null)
//                    .blockNumber(block != null ? block.getNumber() : 0)
//                    .gasUsed(transaction.getGasUsed())
//                    .timestamp(LocalDateTime.now())
//                    .build();
//        } catch (Exception e) {
//            log.error("Error executing withdraw command: {}", e.getMessage(), e);
//            return BlockchainResponse.builder()
//                    .successful(false)
//                    .errorCode("WITHDRAW_ERROR")
//                    .errorMessage(e.getMessage())
//                    .timestamp(LocalDateTime.now())
//                    .build();
//        }
//    }
//
//    /**
//     * Execute a transfer command.
//     *
//     * @param command The transfer command
//     * @return The blockchain response
//     */
//    private BlockchainResponse executeTransferCommand(TransferCommand command) {
//        log.info("Executing transfer command from: {} to: {} amount: {}",
//                command.getFromAccountId(), command.getToAccountId(), command.getAmount());
//
//        try {
//            // Get the source account
//            MockAccount sourceAccount = blockchainState.getAccountById(command.getFromAccountId());
//            if (sourceAccount == null) {
//                return BlockchainResponse.builder()
//                        .successful(false)
//                        .errorCode("SOURCE_ACCOUNT_NOT_FOUND")
//                        .errorMessage("Source account not found: " + command.getFromAccountId())
//                        .timestamp(LocalDateTime.now())
//                        .build();
//            }
//
//            // Get or create the destination account
//            MockAccount destinationAccount = blockchainState.getAccountById(command.getToAccountId());
//            if (destinationAccount == null) {
//                destinationAccount = blockchainState.createAccount(command.getToAccountId(), command.getCurrencyCode());
//            }
//
//            // Check if the source account has sufficient funds
//            if (sourceAccount.getBalance().compareTo(command.getAmount()) < 0) {
//                return BlockchainResponse.builder()
//                        .successful(false)
//                        .errorCode("INSUFFICIENT_FUNDS")
//                        .errorMessage("Insufficient funds in account: " + command.getFromAccountId())
//                        .timestamp(LocalDateTime.now())
//                        .build();
//            }
//
//            // Create a transaction from the source account to the destination account
//            MockTransaction transaction = blockchainState.createTransaction(
//                    command.getFromAccountId(),
//                    command.getToAccountId(),
//                    command.getAmount(),
//                    command.getDescription(),
//                    command.getCurrencyCode()
//            );
//
//            // Mine a block to confirm the transaction
//            MockBlock block = blockchainState.mineBlock();
//
//            return BlockchainResponse.builder()
//                    .successful(true)
//                    .transactionHash(transaction.getHash())
//                    .blockHash(block != null ? block.getHash() : null)
//                    .blockNumber(block != null ? block.getNumber() : 0)
//                    .gasUsed(transaction.getGasUsed())
//                    .timestamp(LocalDateTime.now())
//                    .build();
//        } catch (Exception e) {
//            log.error("Error executing transfer command: {}", e.getMessage(), e);
//            return BlockchainResponse.builder()
//                    .successful(false)
//                    .errorCode("TRANSFER_ERROR")
//                    .errorMessage(e.getMessage())
//                    .timestamp(LocalDateTime.now())
//                    .build();
//        }
//    }
//
//    /**
//     * Convert a mock transaction to a transaction record.
//     *
//     * @param transaction The mock transaction
//     * @return The transaction record
//     */
//    private TransactionRecord convertToTransactionRecord(MockTransaction transaction) {
//        MockAccount fromAccount = blockchainState.getAccountByAddress(transaction.getFrom());
//        MockAccount toAccount = blockchainState.getAccountByAddress(transaction.getTo());
//
//        return TransactionRecord.builder()
//                .hash(transaction.getHash())
//                .transactionHash(transaction.getHash())
//                .from(transaction.getFrom())
//                .to(transaction.getTo())
//                .fromAccount(fromAccount != null ? fromAccount.getId() : null)
//                .toAccount(toAccount != null ? toAccount.getId() : null)
//                .amount(transaction.getAmount())
//                .currencyCode(transaction.getCurrencyCode())
//                .nonce(transaction.getNonce())
//                .gasPrice(transaction.getGasPrice())
//                .gasLimit(transaction.getGasLimit())
//                .gasUsed(transaction.getGasUsed())
//                .createdAt(transaction.getCreatedAt())
//                .confirmedAt(transaction.getConfirmedAt())
//                .blockHash(transaction.getBlockHash())
//                .blockNumber(transaction.getBlockNumber())
//                .transactionIndex(transaction.getTransactionIndex())
//                .status(transaction.getStatus().name())
//                .type(transaction.getType().name())
//                .input(transaction.getInput())
//                .description(transaction.getDescription())
//                .referenceId(transaction.getReferenceId())
//                .build();
//    }
//
//    /**
//     * Get transactions for an account.
//     *
//     * @param accountId The ID of the account
//     * @return The list of transaction records
//     */
//    public List<TransactionRecord> getTransactions(UUID accountId) {
//        return blockchainState.getTransactions(accountId);
//    }
//
//    /**
//     * Mine pending transactions periodically.
//     */
//    @Scheduled(fixedDelayString = "${blockchain.sync.interval.ms:60000}")
//    public void minePendingTransactions() {
//        if (!blockchainConfig.isSyncEnabled()) {
//            return;
//        }
//
//        log.debug("Mining pending transactions");
//
//        try {
//            MockBlock block = blockchainState.mineBlock();
//            if (block != null) {
//                log.info("Mined block: {} with {} transactions", block.getHash(), block.getTransactionCount());
//            }
//        } catch (Exception e) {
//            log.error("Error mining pending transactions: {}", e.getMessage(), e);
//        }
//    }
//
//    /**
//     * Reset the blockchain state.
//     */
//    public void reset() {
//        log.info("Resetting blockchain state");
//        blockchainState.reset();
//    }
//
//    /**
//     * Deposit funds into an account.
//     *
//     * @param accountId    The ID of the account
//     * @param amount       The amount to deposit
//     * @param description  The description of the deposit
//     * @param currencyCode The currency code of the deposit
//     * @return The transaction hash
//     */
//    public String deposit(UUID accountId, BigDecimal amount, String description, String currencyCode) {
//        log.info("Depositing {} {} into account {}", amount, currencyCode, accountId);
//
//        // Ensure the blockchain state is initialized
//        ensureInitialized();
//
//        return blockchainState.deposit(accountId, amount, description, currencyCode);
//    }
//}
