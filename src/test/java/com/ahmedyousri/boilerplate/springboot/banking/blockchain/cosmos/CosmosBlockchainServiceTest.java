//package com.ahmedyousri.boilerplate.springboot.banking.blockchain.cosmos;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//import java.math.BigDecimal;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class CosmosBlockchainServiceTest {
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    private CosmosBlockchainService cosmosBlockchainService;
//
//    private final String REST_ENDPOINT = "http://localhost:1317";
//    private final String RPC_ENDPOINT = "http://localhost:26657";
//    private final String FAUCET_ENDPOINT = "http://localhost:4500";
//    private final String CHAIN_ID = "quicktest";
//
//    @BeforeEach
//    void setUp() {
//        cosmosBlockchainService = new CosmosBlockchainService(restTemplate, REST_ENDPOINT, RPC_ENDPOINT, FAUCET_ENDPOINT, CHAIN_ID);
//    }
//
//    @Test
//    void testGetStatus() {
//        // Arrange
//        String mockResponse = "{\"jsonrpc\":\"2.0\",\"id\":-1,\"result\":{\"node_info\":{\"protocol_version\":{\"p2p\":\"8\",\"block\":\"11\",\"app\":\"0\"},\"id\":\"1234567890abcdef\",\"listen_addr\":\"tcp://0.0.0.0:26656\",\"network\":\"quicktest\",\"version\":\"0.34.19\",\"channels\":\"40202122233038\",\"moniker\":\"local-node\",\"other\":{\"tx_index\":\"on\",\"rpc_address\":\"tcp://0.0.0.0:26657\"}},\"sync_info\":{\"latest_block_hash\":\"ABCDEF1234567890\",\"latest_app_hash\":\"ABCDEF1234567890\",\"latest_block_height\":\"100\",\"latest_block_time\":\"2023-04-07T00:00:00Z\",\"catching_up\":false},\"validator_info\":{\"address\":\"ABCDEF1234567890\",\"pub_key\":{\"type\":\"tendermint/PubKeyEd25519\",\"value\":\"ABCDEF1234567890\"},\"voting_power\":\"100\"}}}";
//        when(restTemplate.getForObject(eq(RPC_ENDPOINT + "/status"), eq(String.class))).thenReturn(mockResponse);
//
//        // Act
//        String result = cosmosBlockchainService.getStatus();
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.contains("node_info"));
//    }
//
//    @Test
//    void testGetBalance() {
//        // Arrange
//        String address = "cosmos186yzfgjzdlgekev8nga3yk8h7zeavxxrzlzrw9";
//        String mockResponse = "{\"balances\":[{\"denom\":\"stake\",\"amount\":\"100000000\"}],\"pagination\":{\"next_key\":null,\"total\":\"1\"}}";
//        when(restTemplate.getForObject(eq(REST_ENDPOINT + "/cosmos/bank/v1beta1/balances/" + address), eq(String.class))).thenReturn(mockResponse);
//
//        // Act
//        String result = cosmosBlockchainService.getBalance(address);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.contains("stake"));
//    }
//
//    @Test
//    void testSendTokens() {
//        // Arrange
//        String fromAddress = "cosmos186yzfgjzdlgekev8nga3yk8h7zeavxxrzlzrw9";
//        String toAddress = "cosmos1wdtem7vfw6ymewcu8dr4rz5xlhjj225ah3k9z0";
//        BigDecimal amount = new BigDecimal("1000");
//        String denom = "stake";
//        String privateKey = "mock_private_key";
//
//        // Mock the faucet response for sendTokensFromFaucet
//        String mockFaucetResponse = "{\"tx_hash\":\"MOCK_TX_HASH_12345\"}";
//        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
//            .thenReturn(ResponseEntity.ok(mockFaucetResponse));
//
//        // Act
//        String result = cosmosBlockchainService.sendTokens(fromAddress, toAddress, amount, denom, privateKey);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.contains("MOCK_TX_HASH_"));
//    }
//
//    @Test
//    void testGetLatestBlocks() {
//        // Arrange
//        int limit = 10;
//        String mockResponse = "{\"jsonrpc\":\"2.0\",\"id\":-1,\"result\":{\"last_height\":\"100\",\"block_metas\":[{\"block_id\":{\"hash\":\"ABCDEF1234567890\",\"parts\":{\"total\":\"1\",\"hash\":\"ABCDEF1234567890\"}},\"block_size\":\"1000\",\"header\":{\"version\":{\"block\":\"11\",\"app\":\"0\"},\"chain_id\":\"quicktest\",\"height\":\"100\",\"time\":\"2023-04-07T00:00:00Z\",\"last_block_id\":{\"hash\":\"ABCDEF1234567890\",\"parts\":{\"total\":\"1\",\"hash\":\"ABCDEF1234567890\"}},\"last_commit_hash\":\"ABCDEF1234567890\",\"data_hash\":\"ABCDEF1234567890\",\"validators_hash\":\"ABCDEF1234567890\",\"next_validators_hash\":\"ABCDEF1234567890\",\"consensus_hash\":\"ABCDEF1234567890\",\"app_hash\":\"ABCDEF1234567890\",\"last_results_hash\":\"ABCDEF1234567890\",\"evidence_hash\":\"ABCDEF1234567890\",\"proposer_address\":\"ABCDEF1234567890\"},\"num_txs\":\"0\"}]}}";
//        when(restTemplate.getForObject(eq(RPC_ENDPOINT + "/blockchain?limit=" + limit), eq(String.class))).thenReturn(mockResponse);
//
//        // Act
//        String result = cosmosBlockchainService.getLatestBlocks(limit);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.contains("block_metas"));
//    }
//}
