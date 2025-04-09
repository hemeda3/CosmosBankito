//package com.ahmedyousri.boilerplate.springboot.banking.blockchain.mock.service;
//
//import model.mock.blockchain.banking.com.ahmedyousri.boilerplate.springboot.MockAccount;
//import model.mock.blockchain.banking.com.ahmedyousri.boilerplate.springboot.MockBlock;
//import model.mock.blockchain.banking.com.ahmedyousri.boilerplate.springboot.MockTransaction;
//import model.blockchain.banking.com.ahmedyousri.boilerplate.springboot.AccountBalance;
//import model.blockchain.banking.com.ahmedyousri.boilerplate.springboot.TransactionRecord;
//import util.banking.com.ahmedyousri.boilerplate.springboot.MoneyUtil;
//import lombok.Getter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.beans.factory.annotation.Autowired;
//import jakarta.annotation.PostConstruct;
//import java.math.BigDecimal;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.stream.Collectors;
//
///**
// * Maintains the state of the mock blockchain.
// * This class is used to simulate a blockchain for testing and development.
// */
//@Component
//public class MockBlockchainState {
//
//    private static final Logger log = LoggerFactory.getLogger(MockBlockchainState.class);
//
//    /**
//     * The map of accounts by ID.
//     */
//    @Getter
//    private final Map<UUID, MockAccount> accountsById = new ConcurrentHashMap<>();
//
//    /**
//     * The map of accounts by address.
//     */
//    @Getter
//    private final Map<String, MockAccount> accountsByAddress = new ConcurrentHashMap<>();
//
//    /**
//     * The map of transactions by hash.
//     */
//    @Getter
//    private final Map<String, MockTransaction> transactionsByHash = new ConcurrentHashMap<>();
//
//    /**
//     * The map of blocks by hash.
//     */
//    @Getter
//    private final Map<String, MockBlock> blocksByHash = new ConcurrentHashMap<>();
//
//    /**
//     * The map of blocks by number.
//     */
//    @Getter
//    private final Map<Long, MockBlock> blocksByNumber = new ConcurrentHashMap<>();
//
//    /**
//     * The list of pending transactions.
//     */
//    @Getter
//    private final List<MockTransaction> pendingTransactions = Collections.synchronizedList(new ArrayList<>());
//
//    /**
//     * The current block number.
//     */
//    private final AtomicLong currentBlockNumber = new AtomicLong(0);
//
//    /**
//     * The genesis block.
//     */
//    @Getter
//    private MockBlock genesisBlock;
//
//    /**
//     * The latest block.
//     */
//    @Getter
//    private MockBlock latestBlock;
//
//    /**
//     * Initialize the mock blockchain state.
//     * This method is called automatically when the bean is created.
//     */
//    @PostConstruct
//    public void initialize() {
//        log.info("Initializing mock blockchain state");
//
//        // Create genesis block
//        genesisBlock = createGenesisBlock();
//        latestBlock = genesisBlock;
//
//        log.info("Mock blockchain initialized with genesis block: {}", genesisBlock.getHash());
//    }
//
//    /**
//     * Ensure the blockchain is initialized.
//     * This method checks if the blockchain has been initialized and initializes it if not.
//     */
//    private void ensureInitialized() {
//        if (genesisBlock == null || latestBlock == null) {
//            initialize();
//        }
//    }
//
//    /**
//     * Create a new account.
//     *
//     * @param id           The ID of the account
//     * @param currencyCode The currency code of the account
//     * @return The created account
//     */
//    public synchronized MockAccount createAccount(UUID id, String currencyCode) {
//        String address = generateAddress(id);
//
//        MockAccount account = MockAccount.builder()
//                .id(id)
//                .address(address)
//                .balance(BigDecimal.ZERO)
//                .currencyCode(currencyCode)
//                .nonce(0)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .transactionHashes(new ArrayList<>())
//                .isContract(false)
//                .status(MockAccount.AccountStatus.ACTIVE)
//                .build();
//
//        accountsById.put(id, account);
//        accountsByAddress.put(address, account);
//
//        log.info("Created account with ID: {} and address: {}", id, address);
//
//        return account;
//    }
//
//    /**
//     * Get an account by ID.
//     *
//     * @param id The ID of the account
//     * @return The account, or null if not found
//     */
//    public MockAccount getAccountById(UUID id) {
//        return accountsById.get(id);
//    }
//
//    /**
//     * Get an account by address.
//     *
//     * @param address The address of the account
//     * @return The account, or null if not found
//     */
//    public MockAccount getAccountByAddress(String address) {
//        return accountsByAddress.get(address);
//    }
//
//    /**
//     * Create a new transaction.
//     *
//     * @param fromAccountId The ID of the sender account
//     * @param toAccountId   The ID of the recipient account
//     * @param amount        The amount of the transaction
//     * @param description   The description of the transaction
//     * @param currencyCode  The currency code of the transaction
//     * @return The created transaction
//     * @throws IllegalArgumentException If the sender or recipient account is not found
//     * @throws IllegalStateException    If the sender account has insufficient funds
//     */
//    public synchronized MockTransaction createTransaction(
//            UUID fromAccountId,
//            UUID toAccountId,
//            BigDecimal amount,
//            String description,
//            String currencyCode
//    ) {
//        MockAccount fromAccount = getAccountById(fromAccountId);
//        if (fromAccount == null) {
//            throw new IllegalArgumentException("Sender account not found: " + fromAccountId);
//        }
//
//        MockAccount toAccount = getAccountById(toAccountId);
//        if (toAccount == null) {
//            throw new IllegalArgumentException("Recipient account not found: " + toAccountId);
//        }
//
//        // Check if the sender has sufficient funds
//        if (fromAccount.getBalance().compareTo(amount) < 0) {
//            throw new IllegalStateException("Insufficient funds in account: " + fromAccountId);
//        }
//
//        // Create the transaction
//        UUID transactionId = UUID.randomUUID();
//        String hash = generateTransactionHash(fromAccount.getAddress(), toAccount.getAddress(), amount, fromAccount.getNonce());
//
//        MockTransaction transaction = MockTransaction.builder()
//                .id(transactionId)
//                .hash(hash)
//                .from(fromAccount.getAddress())
//                .to(toAccount.getAddress())
//                .amount(amount)
//                .currencyCode(currencyCode)
//                .nonce(fromAccount.incrementNonce())
//                .gasPrice(21000000000L)
//                .gasLimit(21000)
//                .gasUsed(21000)
//                .createdAt(LocalDateTime.now())
//                .status(MockTransaction.TransactionStatus.PENDING)
//                .type(MockTransaction.TransactionType.TRANSFER)
//                .description(description)
//                .referenceId(transactionId.toString())
//                .metadata(new HashMap<>())
//                .build();
//
//        // Add the transaction to the pending transactions
//        pendingTransactions.add(transaction);
//        transactionsByHash.put(hash, transaction);
//
//        // Add the transaction hash to the accounts
//        fromAccount.addTransactionHash(hash);
//        toAccount.addTransactionHash(hash);
//
//        log.info("Created transaction with hash: {} from: {} to: {} amount: {}", hash, fromAccountId, toAccountId, amount);
//
//        return transaction;
//    }
//
//    /**
//     * Process pending transactions and create a new block.
//     *
//     * @return The created block
//     */
//    public synchronized MockBlock mineBlock() {
//        // Ensure the blockchain is initialized
//        ensureInitialized();
//
//        if (pendingTransactions.isEmpty()) {
//            log.info("No pending transactions to mine");
//            return null;
//        }
//
//        // Create a new block
//        long blockNumber = currentBlockNumber.incrementAndGet();
//        String blockHash = generateBlockHash(blockNumber, latestBlock.getHash(), pendingTransactions);
//
//        MockBlock block = MockBlock.builder()
//                .number(blockNumber)
//                .hash(blockHash)
//                .parentHash(latestBlock.getHash())
//                .timestamp(LocalDateTime.now())
//                .miner("0x0000000000000000000000000000000000000000")
//                .difficulty(1)
//                .totalDifficulty(latestBlock.getTotalDifficulty() + 1)
//                .size(1000)
//                .gasLimit(8000000)
//                .gasUsed(0)
//                .nonce("0x0000000000000000")
//                .extraData("0x")
//                .transactionHashes(new ArrayList<>())
//                .transactions(new ArrayList<>())
//                .status(MockBlock.BlockStatus.PENDING)
//                .build();
//
//        // Add transactions to the block
//        List<MockTransaction> transactionsToProcess = new ArrayList<>(pendingTransactions);
//        long totalGasUsed = 0;
//
//        for (MockTransaction transaction : transactionsToProcess) {
//            // Process the transaction
//            try {
//                processTransaction(transaction);
//
//                // Add the transaction to the block
//                block.addTransaction(transaction);
//                totalGasUsed += transaction.getGasUsed();
//            } catch (Exception e) {
//                log.error("Failed to process transaction: {}", transaction.getHash(), e);
//                transaction.fail(e.getMessage());
//            }
//        }
//
//        // Update block gas used
//        block.setGasUsed(totalGasUsed);
//
//        // Confirm the block
//        block.confirm();
//
//        // Add the block to the blockchain
//        blocksByHash.put(blockHash, block);
//        blocksByNumber.put(blockNumber, block);
//        latestBlock = block;
//
//        // Clear pending transactions
//        pendingTransactions.removeAll(transactionsToProcess);
//
//        log.info("Mined block: {} with {} transactions", blockHash, block.getTransactionCount());
//
//        return block;
//    }
//
//    /**
//     * Process a transaction.
//     *
//     * @param transaction The transaction to process
//     * @throws IllegalArgumentException If the sender or recipient account is not found
//     * @throws IllegalStateException    If the sender account has insufficient funds
//     */
//    private void processTransaction(MockTransaction transaction) {
//        MockAccount fromAccount = getAccountByAddress(transaction.getFrom());
//        if (fromAccount == null) {
//            throw new IllegalArgumentException("Sender account not found: " + transaction.getFrom());
//        }
//
//        MockAccount toAccount = getAccountByAddress(transaction.getTo());
//        if (toAccount == null) {
//            throw new IllegalArgumentException("Recipient account not found: " + transaction.getTo());
//        }
//
//        // Check if the sender has sufficient funds
//        if (fromAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
//            throw new IllegalStateException("Insufficient funds in account: " + fromAccount.getId());
//        }
//
//        // Transfer the funds
//        fromAccount.debit(transaction.getAmount());
//        toAccount.credit(transaction.getAmount());
//
//        // Update the transaction status
//        transaction.setStatus(MockTransaction.TransactionStatus.CONFIRMED);
//    }
//
//    /**
//     * Create the genesis block.
//     *
//     * @return The genesis block
//     */
//    private MockBlock createGenesisBlock() {
//        String blockHash = generateBlockHash(0, "0x0000000000000000000000000000000000000000000000000000000000000000", Collections.emptyList());
//
//        MockBlock block = MockBlock.builder()
//                .number(0)
//                .hash(blockHash)
//                .parentHash("0x0000000000000000000000000000000000000000000000000000000000000000")
//                .timestamp(LocalDateTime.now())
//                .miner("0x0000000000000000000000000000000000000000")
//                .difficulty(1)
//                .totalDifficulty(1)
//                .size(1000)
//                .gasLimit(8000000)
//                .gasUsed(0)
//                .nonce("0x0000000000000000")
//                .extraData("0x")
//                .transactionHashes(new ArrayList<>())
//                .transactions(new ArrayList<>())
//                .status(MockBlock.BlockStatus.CONFIRMED)
//                .build();
//
//        blocksByHash.put(blockHash, block);
//        blocksByNumber.put(0L, block);
//
//        return block;
//    }
//
//    /**
//     * Generate a blockchain address for an account.
//     *
//     * @param id The ID of the account
//     * @return The generated address
//     */
//    private String generateAddress(UUID id) {
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] hash = digest.digest(id.toString().getBytes());
//
//            // Convert the hash to a hex string
//            StringBuilder hexString = new StringBuilder();
//            for (byte b : hash) {
//                String hex = Integer.toHexString(0xff & b);
//                if (hex.length() == 1) {
//                    hexString.append('0');
//                }
//                hexString.append(hex);
//            }
//
//            // Return the first 40 characters prefixed with "0x"
//            return "0x" + hexString.substring(0, 40);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("Failed to generate address", e);
//        }
//    }
//
//    /**
//     * Generate a hash for a transaction.
//     *
//     * @param from  The address of the sender
//     * @param to    The address of the recipient
//     * @param amount The amount of the transaction
//     * @param nonce The nonce of the transaction
//     * @return The generated hash
//     */
//    private String generateTransactionHash(String from, String to, BigDecimal amount, long nonce) {
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            String data = from + to + amount.toString() + nonce + System.nanoTime();
//            byte[] hash = digest.digest(data.getBytes());
//
//            // Convert the hash to a hex string
//            StringBuilder hexString = new StringBuilder();
//            for (byte b : hash) {
//                String hex = Integer.toHexString(0xff & b);
//                if (hex.length() == 1) {
//                    hexString.append('0');
//                }
//                hexString.append(hex);
//            }
//
//            // Return the hash prefixed with "0x"
//            return "0x" + hexString.toString();
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("Failed to generate transaction hash", e);
//        }
//    }
//
//    /**
//     * Generate a hash for a block.
//     *
//     * @param blockNumber The number of the block
//     * @param parentHash  The hash of the parent block
//     * @param transactions The list of transactions in the block
//     * @return The generated hash
//     */
//    private String generateBlockHash(long blockNumber, String parentHash, List<MockTransaction> transactions) {
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//
//            // Concatenate transaction hashes
//            String transactionHashes = transactions.stream()
//                    .map(MockTransaction::getHash)
//                    .collect(Collectors.joining());
//
//            String data = blockNumber + parentHash + transactionHashes + System.nanoTime();
//            byte[] hash = digest.digest(data.getBytes());
//
//            // Convert the hash to a hex string
//            StringBuilder hexString = new StringBuilder();
//            for (byte b : hash) {
//                String hex = Integer.toHexString(0xff & b);
//                if (hex.length() == 1) {
//                    hexString.append('0');
//                }
//                hexString.append(hex);
//            }
//
//            // Return the hash prefixed with "0x"
//            return "0x" + hexString.toString();
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("Failed to generate block hash", e);
//        }
//    }
//
//    /**
//     * Get the balance of an account.
//     *
//     * @param accountId The ID of the account
//     * @return The balance of the account
//     * @throws IllegalArgumentException If the account is not found
//     */
//    public BigDecimal getBalance(UUID accountId) {
//        MockAccount account = getAccountById(accountId);
//        if (account == null) {
//            throw new IllegalArgumentException("Account not found: " + accountId);
//        }
//
//        return MoneyUtil.round(account.getBalance());
//    }
//
//    /**
//     * Get a transaction by hash.
//     *
//     * @param hash The hash of the transaction
//     * @return The transaction, or null if not found
//     */
//    public MockTransaction getTransactionByHash(String hash) {
//        return transactionsByHash.get(hash);
//    }
//
//    /**
//     * Get a block by hash.
//     *
//     * @param hash The hash of the block
//     * @return The block, or null if not found
//     */
//    public MockBlock getBlockByHash(String hash) {
//        return blocksByHash.get(hash);
//    }
//
//    /**
//     * Get a block by number.
//     *
//     * @param number The number of the block
//     * @return The block, or null if not found
//     */
//    public MockBlock getBlockByNumber(long number) {
//        return blocksByNumber.get(number);
//    }
//
//    /**
//     * Get the current block number.
//     *
//     * @return The current block number
//     */
//    public long getCurrentBlockNumber() {
//        return currentBlockNumber.get();
//    }
//
//    /**
//     * Get the transaction count for an account.
//     *
//     * @param accountId The ID of the account
//     * @return The transaction count
//     * @throws IllegalArgumentException If the account is not found
//     */
//    public int getTransactionCount(UUID accountId) {
//        MockAccount account = getAccountById(accountId);
//        if (account == null) {
//            throw new IllegalArgumentException("Account not found: " + accountId);
//        }
//
//        return account.getTransactionHashes().size();
//    }
//
//    /**
//     * Get the transaction history for an account.
//     *
//     * @param accountId The ID of the account
//     * @return The list of transactions
//     * @throws IllegalArgumentException If the account is not found
//     */
//    public List<MockTransaction> getTransactionHistory(UUID accountId) {
//        MockAccount account = getAccountById(accountId);
//        if (account == null) {
//            throw new IllegalArgumentException("Account not found: " + accountId);
//        }
//
//        return account.getTransactionHashes().stream()
//                .map(this::getTransactionByHash)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Get transactions for an account.
//     *
//     * @param accountId The ID of the account
//     * @return The list of transaction records
//     * @throws IllegalArgumentException If the account is not found
//     */
//    public List<TransactionRecord> getTransactions(UUID accountId) {
//        List<MockTransaction> transactions = getTransactionHistory(accountId);
//
//        return transactions.stream()
//                .map(this::convertToTransactionRecord)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Get a transaction by hash.
//     *
//     * @param hash The hash of the transaction
//     * @return The transaction record
//     */
//    public TransactionRecord getTransaction(String hash) {
//        MockTransaction transaction = getTransactionByHash(hash);
//        if (transaction == null) {
//            return null;
//        }
//
//        return convertToTransactionRecord(transaction);
//    }
//
//    /**
//     * Convert a MockTransaction to a TransactionRecord.
//     *
//     * @param transaction The mock transaction
//     * @return The transaction record
//     */
//    private TransactionRecord convertToTransactionRecord(MockTransaction transaction) {
//        MockAccount fromAccount = getAccountByAddress(transaction.getFrom());
//        MockAccount toAccount = getAccountByAddress(transaction.getTo());
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
//                .blockNumber(transaction.getBlockNumber())
//                .status(transaction.getStatus().name())
//                .type(transaction.getType().name())
//                .description(transaction.getDescription())
//                .referenceId(transaction.getReferenceId())
//                .build();
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
//        // Ensure the blockchain is initialized
//        ensureInitialized();
//
//        // Get or create the account
//        MockAccount account = getAccountById(accountId);
//        if (account == null) {
//            account = createAccount(accountId, currencyCode);
//        }
//
//        // Create a transaction from the system account to the target account
//        UUID systemAccountId = UUID.fromString("00000000-0000-0000-0000-000000000001");
//        MockAccount systemAccount = getAccountById(systemAccountId);
//        if (systemAccount == null) {
//            systemAccount = createAccount(systemAccountId, currencyCode);
//            // Give the system account a large balance
//            systemAccount.credit(new BigDecimal("1000000.00"));
//        } else {
//            // Ensure the system account has enough balance
//            if (systemAccount.getBalance().compareTo(amount) < 0) {
//                systemAccount.credit(new BigDecimal("1000000.00"));
//            }
//        }
//
//        // Create a transaction (the actual crediting will happen during mineBlock)
//        MockTransaction transaction = createTransaction(
//                systemAccountId,
//                accountId,
//                amount,
//                description,
//                currencyCode
//        );
//
//        // Mine a block to confirm the transaction
//        mineBlock();
//
//        return transaction.getHash();
//    }
//
//    /**
//     * Get the account balance.
//     *
//     * @param accountId The ID of the account
//     * @return The account balance
//     */
//    public AccountBalance getAccountBalance(UUID accountId) {
//        log.info("Getting account balance for account: {}", accountId);
//
//        MockAccount account = getAccountById(accountId);
//        if (account == null) {
//            throw new IllegalArgumentException("Account not found: " + accountId);
//        }
//
//        return AccountBalance.builder()
//                .accountId(accountId)
//                .balance(account.getBalance())
//                .currencyCode(account.getCurrencyCode())
//                .address(account.getAddress())
//                .blockNumber(getCurrentBlockNumber())
//                .timestamp(LocalDateTime.now())
//                .build();
//    }
//
//    /**
//     * Reset the blockchain state.
//     */
//    public synchronized void reset() {
//        log.info("Resetting mock blockchain state");
//
//        accountsById.clear();
//        accountsByAddress.clear();
//        transactionsByHash.clear();
//        blocksByHash.clear();
//        blocksByNumber.clear();
//        pendingTransactions.clear();
//        currentBlockNumber.set(0);
//
//        // Create genesis block
//        genesisBlock = createGenesisBlock();
//        latestBlock = genesisBlock;
//
//        log.info("Mock blockchain reset with genesis block: {}", genesisBlock.getHash());
//    }
//}
