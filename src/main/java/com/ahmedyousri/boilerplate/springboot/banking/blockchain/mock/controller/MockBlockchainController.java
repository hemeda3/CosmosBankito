//package com.ahmedyousri.boilerplate.springboot.banking.blockchain.mock.controller;
//
//import command.blockchain.banking.com.ahmedyousri.boilerplate.springboot.DepositCommand;
//import model.mock.blockchain.banking.com.ahmedyousri.boilerplate.springboot.MockAccount;
//import model.mock.blockchain.banking.com.ahmedyousri.boilerplate.springboot.MockBlock;
//import model.mock.blockchain.banking.com.ahmedyousri.boilerplate.springboot.MockTransaction;
//import com.ahmedyousri.boilerplate.springboot.banking.blockchain.mock.service.MockBlockchainService;
//import com.ahmedyousri.boilerplate.springboot.banking.blockchain.mock.service.MockBlockchainState;
//import model.blockchain.banking.com.ahmedyousri.boilerplate.springboot.AccountBalance;
//import model.blockchain.banking.com.ahmedyousri.boilerplate.springboot.BlockchainResponse;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
///**
// * Controller for the mock blockchain explorer API.
// * This controller provides endpoints for exploring the mock blockchain.
// */
//@RestController
//@RequestMapping("/api/v1/blockchain")
//@ConditionalOnProperty(name = "blockchain.mock.enabled", havingValue = "true", matchIfMissing = true)
//@RequiredArgsConstructor
//public class MockBlockchainController {
//
//    private static final Logger log = LoggerFactory.getLogger(MockBlockchainController.class);
//
//    private final MockBlockchainState blockchainState;
//    private final MockBlockchainService blockchainService;
//
//    /**
//     * Deposit funds into an account.
//     *
//     * @param accountId    The ID of the account
//     * @param amount       The amount to deposit
//     * @param description  The description of the deposit
//     * @param currencyCode The currency code of the deposit
//     * @return The blockchain response
//     */
//    @PostMapping("/deposit")
//    public ResponseEntity<BlockchainResponse> deposit(
//            @RequestParam String accountId,
//            @RequestParam String amount,
//            @RequestParam(required = false) String description,
//            @RequestParam String currencyCode
//    ) {
//        log.info("Depositing {} {} into account {}", amount, currencyCode, accountId);
//
//        // Ensure the blockchain state is initialized
//        ensureInitialized();
//
//        try {
//            UUID accountUuid = UUID.fromString(accountId);
//            BigDecimal amountDecimal = new BigDecimal(amount);
//
//            DepositCommand depositCommand = new DepositCommand(accountUuid, amountDecimal, description, currencyCode);
//            BlockchainResponse response = blockchainService.executeCommand(depositCommand);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            log.error("Error depositing funds: {}", e.getMessage(), e);
//
//            BlockchainResponse errorResponse = BlockchainResponse.builder()
//                    .successful(false)
//                    .errorCode("DEPOSIT_ERROR")
//                    .errorMessage(e.getMessage())
//                    .timestamp(LocalDateTime.now())
//                    .build();
//
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
//
//    /**
//     * Get the account balance.
//     *
//     * @param accountId The ID of the account
//     * @return The account balance
//     */
//    @GetMapping("/accounts/{accountId}/balance/details")
//    public ResponseEntity<AccountBalance> getAccountBalance(@PathVariable String accountId) {
//        log.info("Getting account balance details: {}", accountId);
//
//        // Ensure the blockchain state is initialized
//        ensureInitialized();
//
//        try {
//            UUID accountUuid = UUID.fromString(accountId);
//            AccountBalance balance = blockchainService.getAccountBalance(accountUuid);
//
//            return ResponseEntity.ok(balance);
//        } catch (Exception e) {
//            log.error("Error getting account balance: {}", e.getMessage(), e);
//            return ResponseEntity.notFound().build();
//        }
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
//    /**
//     * Get the status of the blockchain.
//     *
//     * @return The blockchain status
//     */
//    @GetMapping("/status")
//    public ResponseEntity<Map<String, Object>> getStatus() {
//        log.info("Getting blockchain status");
//
//        // Ensure the blockchain state is initialized
//        ensureInitialized();
//
//        Map<String, Object> status = new HashMap<>();
//        status.put("blockNumber", blockchainState.getCurrentBlockNumber());
//        status.put("accountCount", blockchainState.getAccountsById().size());
//        status.put("transactionCount", blockchainState.getTransactionsByHash().size());
//        status.put("pendingTransactionCount", blockchainState.getPendingTransactions().size());
//
//        return ResponseEntity.ok(status);
//    }
//
//    /**
//     * Get a list of accounts.
//     *
//     * @return The list of accounts
//     */
//    @GetMapping("/accounts")
//    public ResponseEntity<List<Map<String, Object>>> getAccounts() {
//        log.info("Getting accounts");
//
//        List<Map<String, Object>> accounts = blockchainState.getAccountsById().values().stream()
//                .map(account -> {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("id", account.getId());
//                    map.put("address", account.getAddress());
//                    map.put("balance", account.getBalance());
//                    map.put("currencyCode", account.getCurrencyCode());
//                    map.put("transactionCount", account.getTransactionHashes().size());
//                    map.put("status", account.getStatus().name());
//                    return map;
//                })
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(accounts);
//    }
//
//    /**
//     * Get an account by ID.
//     *
//     * @param accountId The ID of the account
//     * @return The account
//     */
//    @GetMapping("/accounts/{accountId}")
//    public ResponseEntity<Map<String, Object>> getAccount(@PathVariable UUID accountId) {
//        log.info("Getting account: {}", accountId);
//
//        MockAccount account = blockchainState.getAccountById(accountId);
//        if (account == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Map<String, Object> accountData = new HashMap<>();
//        accountData.put("id", account.getId());
//        accountData.put("address", account.getAddress());
//        accountData.put("balance", account.getBalance());
//        accountData.put("currencyCode", account.getCurrencyCode());
//        accountData.put("nonce", account.getNonce());
//        accountData.put("createdAt", account.getCreatedAt());
//        accountData.put("updatedAt", account.getUpdatedAt());
//        accountData.put("transactionCount", account.getTransactionHashes().size());
//        accountData.put("status", account.getStatus().name());
//
//        return ResponseEntity.ok(accountData);
//    }
//
//    /**
//     * Get the balance of an account.
//     *
//     * @param accountId The ID of the account
//     * @return The account balance
//     */
//    @GetMapping("/accounts/{accountId}/balance")
//    public ResponseEntity<Map<String, Object>> getAccountBalanceMap(@PathVariable UUID accountId) {
//        log.info("Getting account balance: {}", accountId);
//
//        try {
//            BigDecimal balance = blockchainState.getBalance(accountId);
//
//            Map<String, Object> balanceData = new HashMap<>();
//            balanceData.put("accountId", accountId);
//            balanceData.put("balance", balance);
//            balanceData.put("blockNumber", blockchainState.getCurrentBlockNumber());
//
//            return ResponseEntity.ok(balanceData);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    /**
//     * Get the transaction history of an account.
//     *
//     * @param accountId The ID of the account
//     * @param limit     The maximum number of transactions to return
//     * @return The transaction history
//     */
//    @GetMapping("/accounts/{accountId}/transactions")
//    public ResponseEntity<List<Map<String, Object>>> getAccountTransactions(
//            @PathVariable UUID accountId,
//            @RequestParam(defaultValue = "10") int limit
//    ) {
//        log.info("Getting account transactions: {}", accountId);
//
//        try {
//            List<MockTransaction> transactions = blockchainState.getTransactionHistory(accountId);
//
//            List<Map<String, Object>> transactionData = transactions.stream()
//                    .limit(limit)
//                    .map(transaction -> {
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("hash", transaction.getHash());
//                        map.put("from", transaction.getFrom());
//                        map.put("to", transaction.getTo());
//                        map.put("amount", transaction.getAmount());
//                        map.put("currencyCode", transaction.getCurrencyCode());
//                        map.put("status", transaction.getStatus().name());
//                        map.put("createdAt", transaction.getCreatedAt());
//                        map.put("confirmedAt", transaction.getConfirmedAt() != null ? transaction.getConfirmedAt() : null);
//                        map.put("blockHash", transaction.getBlockHash() != null ? transaction.getBlockHash() : null);
//                        map.put("blockNumber", transaction.getBlockNumber());
//                        map.put("description", transaction.getDescription() != null ? transaction.getDescription() : null);
//                        return map;
//                    })
//                    .collect(Collectors.toList());
//
//            return ResponseEntity.ok(transactionData);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    /**
//     * Get a list of blocks.
//     *
//     * @param limit The maximum number of blocks to return
//     * @return The list of blocks
//     */
//    @GetMapping("/blocks")
//    public ResponseEntity<List<Map<String, Object>>> getBlocks(@RequestParam(defaultValue = "10") int limit) {
//        log.info("Getting blocks");
//
//        List<Map<String, Object>> blocks = blockchainState.getBlocksByNumber().values().stream()
//                .sorted((b1, b2) -> Long.compare(b2.getNumber(), b1.getNumber())) // Sort by block number descending
//                .limit(limit)
//                .map(block -> {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("number", block.getNumber());
//                    map.put("hash", block.getHash());
//                    map.put("timestamp", block.getTimestamp());
//                    map.put("transactionCount", block.getTransactionCount());
//                    map.put("status", block.getStatus().name());
//                    return map;
//                })
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(blocks);
//    }
//
//    /**
//     * Get a block by number.
//     *
//     * @param blockNumber The number of the block
//     * @return The block
//     */
//    @GetMapping("/blocks/{blockNumber}")
//    public ResponseEntity<Map<String, Object>> getBlock(@PathVariable long blockNumber) {
//        log.info("Getting block: {}", blockNumber);
//
//        MockBlock block = blockchainState.getBlockByNumber(blockNumber);
//        if (block == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Map<String, Object> blockData = new HashMap<>();
//        blockData.put("number", block.getNumber());
//        blockData.put("hash", block.getHash());
//        blockData.put("parentHash", block.getParentHash());
//        blockData.put("timestamp", block.getTimestamp());
//        blockData.put("miner", block.getMiner());
//        blockData.put("difficulty", block.getDifficulty());
//        blockData.put("totalDifficulty", block.getTotalDifficulty());
//        blockData.put("size", block.getSize());
//        blockData.put("gasLimit", block.getGasLimit());
//        blockData.put("gasUsed", block.getGasUsed());
//        blockData.put("nonce", block.getNonce());
//        blockData.put("extraData", block.getExtraData());
//        blockData.put("transactionCount", block.getTransactionCount());
//        blockData.put("status", block.getStatus().name());
//        blockData.put("transactions", block.getTransactionHashes());
//
//        return ResponseEntity.ok(blockData);
//    }
//
//    /**
//     * Get a list of transactions.
//     *
//     * @param limit The maximum number of transactions to return
//     * @return The list of transactions
//     */
//    @GetMapping("/transactions")
//    public ResponseEntity<List<Map<String, Object>>> getTransactions(@RequestParam(defaultValue = "10") int limit) {
//        log.info("Getting transactions");
//
//        List<Map<String, Object>> transactions = blockchainState.getTransactionsByHash().values().stream()
//                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt())) // Sort by creation time descending
//                .limit(limit)
//                .map(transaction -> {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("hash", transaction.getHash());
//                    map.put("from", transaction.getFrom());
//                    map.put("to", transaction.getTo());
//                    map.put("amount", transaction.getAmount());
//                    map.put("currencyCode", transaction.getCurrencyCode());
//                    map.put("status", transaction.getStatus().name());
//                    map.put("createdAt", transaction.getCreatedAt());
//                    map.put("blockNumber", transaction.getBlockNumber());
//                    return map;
//                })
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(transactions);
//    }
//
//    /**
//     * Get a transaction by hash.
//     *
//     * @param transactionHash The hash of the transaction
//     * @return The transaction
//     */
//    @GetMapping("/transactions/{transactionHash}")
//    public ResponseEntity<Map<String, Object>> getTransaction(@PathVariable String transactionHash) {
//        log.info("Getting transaction: {}", transactionHash);
//
//        MockTransaction transaction = blockchainState.getTransactionByHash(transactionHash);
//        if (transaction == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Map<String, Object> transactionData = new HashMap<>();
//        transactionData.put("hash", transaction.getHash());
//        transactionData.put("from", transaction.getFrom());
//        transactionData.put("to", transaction.getTo());
//        transactionData.put("amount", transaction.getAmount());
//        transactionData.put("currencyCode", transaction.getCurrencyCode());
//        transactionData.put("nonce", transaction.getNonce());
//        transactionData.put("gasPrice", transaction.getGasPrice());
//        transactionData.put("gasLimit", transaction.getGasLimit());
//        transactionData.put("gasUsed", transaction.getGasUsed());
//        transactionData.put("createdAt", transaction.getCreatedAt());
//        transactionData.put("confirmedAt", transaction.getConfirmedAt() != null ? transaction.getConfirmedAt() : null);
//        transactionData.put("blockHash", transaction.getBlockHash() != null ? transaction.getBlockHash() : null);
//        transactionData.put("blockNumber", transaction.getBlockNumber());
//        transactionData.put("transactionIndex", transaction.getTransactionIndex());
//        transactionData.put("status", transaction.getStatus().name());
//        transactionData.put("type", transaction.getType().name());
//        transactionData.put("input", transaction.getInput() != null ? transaction.getInput() : null);
//        transactionData.put("description", transaction.getDescription() != null ? transaction.getDescription() : null);
//        transactionData.put("referenceId", transaction.getReferenceId() != null ? transaction.getReferenceId() : null);
//
//        return ResponseEntity.ok(transactionData);
//    }
//
//    /**
//     * Get a list of pending transactions.
//     *
//     * @return The list of pending transactions
//     */
//    @GetMapping("/pending-transactions")
//    public ResponseEntity<List<Map<String, Object>>> getPendingTransactions() {
//        log.info("Getting pending transactions");
//
//        List<Map<String, Object>> pendingTransactions = blockchainState.getPendingTransactions().stream()
//                .map(transaction -> {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("hash", transaction.getHash());
//                    map.put("from", transaction.getFrom());
//                    map.put("to", transaction.getTo());
//                    map.put("amount", transaction.getAmount());
//                    map.put("currencyCode", transaction.getCurrencyCode());
//                    map.put("nonce", transaction.getNonce());
//                    map.put("createdAt", transaction.getCreatedAt());
//                    map.put("description", transaction.getDescription() != null ? transaction.getDescription() : null);
//                    return map;
//                })
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(pendingTransactions);
//    }
//
//    /**
//     * Mine pending transactions.
//     *
//     * @return The mined block
//     */
//    @PostMapping("/mine")
//    public ResponseEntity<Map<String, Object>> mine() {
//        log.info("Mining pending transactions");
//
//        MockBlock block = blockchainState.mineBlock();
//        if (block == null) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("message", "No pending transactions to mine");
//            return ResponseEntity.ok(response);
//        }
//
//        Map<String, Object> blockData = new HashMap<>();
//        blockData.put("number", block.getNumber());
//        blockData.put("hash", block.getHash());
//        blockData.put("timestamp", block.getTimestamp());
//        blockData.put("transactionCount", block.getTransactionCount());
//        blockData.put("status", block.getStatus().name());
//        blockData.put("message", "Successfully mined block with " + block.getTransactionCount() + " transactions");
//
//        return ResponseEntity.ok(blockData);
//    }
//
//    /**
//     * Reset the blockchain state.
//     *
//     * @return A success message
//     */
//    @PostMapping("/reset")
//    public ResponseEntity<Map<String, Object>> reset() {
//        log.info("Resetting blockchain state");
//
//        blockchainService.reset();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "Blockchain state reset successfully");
//        return ResponseEntity.ok(response);
//    }
//}
