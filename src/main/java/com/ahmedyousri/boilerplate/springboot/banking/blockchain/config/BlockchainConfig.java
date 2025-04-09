package com.ahmedyousri.boilerplate.springboot.banking.blockchain.config;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration for the blockchain integration.
 * This class loads configuration properties from the blockchain.properties file.
 */
@Configuration
@PropertySource("classpath:blockchain.properties")
@Getter
public class BlockchainConfig {
    
    private static final Logger log = LoggerFactory.getLogger(BlockchainConfig.class);
    
    @Value("${blockchain.provider.url:http://localhost:8545}")
    private String providerUrl;
    
    @Value("${blockchain.network.id:1337}")
    private int networkId;
    
    @Value("${blockchain.gas.limit:6721975}")
    private long gasLimit;
    
    @Value("${blockchain.gas.price:20000000000}")
    private long gasPrice;
    
    @Value("${blockchain.confirmation.blocks:1}")
    private int confirmationBlocks;
    
    @Value("${blockchain.timeout.ms:30000}")
    private long timeoutMs;
    
    @Value("${blockchain.mock.enabled:false}")
    private boolean mockEnabled;
    
    @Value("${blockchain.testnet.enabled:false}")
    private boolean testnetEnabled;
    
    @Value("${blockchain.production.enabled:false}")
    private boolean productionEnabled;
    
    @Value("${blockchain.cosmos.rest.endpoint:http://localhost:1317}")
    private String cosmosRestEndpoint;
    
    @Value("${blockchain.cosmos.rpc.endpoint:http://localhost:26657}")
    private String cosmosRpcEndpoint;
    
    @Value("${blockchain.cosmos.chain.id:quicktest}")
    private String cosmosChainId;
    
    @Value("${blockchain.system.account.address:0x0123456789abcdef0123456789abcdef01234567}")
    private String systemAccountAddress;
    
    @Value("${blockchain.system.account.privateKey:0x0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef}")
    private String systemAccountPrivateKey;
    
    @Value("${blockchain.contract.address:0x0123456789abcdef0123456789abcdef01234567}")
    private String contractAddress;
    
    @Value("${blockchain.contract.auto.deploy:false}")
    private boolean contractAutoDeploy;
    
    @Value("${blockchain.contract.abi:[]}")
    private String contractAbi;
    
    @Value("${blockchain.contract.bytecode:0x}")
    private String contractBytecode;
    
    @Value("${blockchain.retry.max.attempts:3}")
    private int retryMaxAttempts;
    
    @Value("${blockchain.retry.delay.ms:1000}")
    private long retryDelayMs;
    
    @Value("${blockchain.events.enabled:true}")
    private boolean eventsEnabled;
    
    @Value("${blockchain.events.polling.interval.ms:15000}")
    private long eventsPollingIntervalMs;
    
    @Value("${blockchain.transaction.receipts.enabled:true}")
    private boolean transactionReceiptsEnabled;
    
    @Value("${blockchain.sync.enabled:true}")
    private boolean syncEnabled;
    
    @Value("${blockchain.sync.interval.ms:60000}")
    private long syncIntervalMs;
    
    @Value("${blockchain.metrics.enabled:true}")
    private boolean metricsEnabled;
    
    @Value("${blockchain.transaction.batching.enabled:false}")
    private boolean transactionBatchingEnabled;
    
    @Value("${blockchain.transaction.batching.max.size:10}")
    private int transactionBatchingMaxSize;
    
    @Value("${blockchain.transaction.batching.max.wait.ms:5000}")
    private long transactionBatchingMaxWaitMs;
    
    /**
     * Log the configuration.
     */
    public void logConfig() {
        log.info("Blockchain Configuration:");
        log.info("Provider URL: {}", providerUrl);
        log.info("Network ID: {}", networkId);
        log.info("Gas Limit: {}", gasLimit);
        log.info("Gas Price: {}", gasPrice);
        log.info("Confirmation Blocks: {}", confirmationBlocks);
        log.info("Timeout (ms): {}", timeoutMs);
        log.info("Mock Enabled: {}", mockEnabled);
        log.info("Testnet Enabled: {}", testnetEnabled);
        log.info("Production Enabled: {}", productionEnabled);
        log.info("Cosmos REST Endpoint: {}", cosmosRestEndpoint);
        log.info("Cosmos RPC Endpoint: {}", cosmosRpcEndpoint);
        log.info("Cosmos Chain ID: {}", cosmosChainId);
        log.info("System Account Address: {}", systemAccountAddress);
        log.info("Contract Address: {}", contractAddress);
        log.info("Contract Auto Deploy: {}", contractAutoDeploy);
        log.info("Retry Max Attempts: {}", retryMaxAttempts);
        log.info("Retry Delay (ms): {}", retryDelayMs);
        log.info("Events Enabled: {}", eventsEnabled);
        log.info("Events Polling Interval (ms): {}", eventsPollingIntervalMs);
        log.info("Transaction Receipts Enabled: {}", transactionReceiptsEnabled);
        log.info("Sync Enabled: {}", syncEnabled);
        log.info("Sync Interval (ms): {}", syncIntervalMs);
        log.info("Metrics Enabled: {}", metricsEnabled);
        log.info("Transaction Batching Enabled: {}", transactionBatchingEnabled);
        log.info("Transaction Batching Max Size: {}", transactionBatchingMaxSize);
        log.info("Transaction Batching Max Wait (ms): {}", transactionBatchingMaxWaitMs);
    }
}
