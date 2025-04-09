package com.ahmedyousri.boilerplate.springboot.banking.blockchain.cosmos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for Cosmos SDK integration
 */
@Configuration
public class CosmosSDKConfiguration {

    @Value("${cosmos.node.rest.endpoint:http://localhost:1317}")
    private String restEndpoint;

    @Value("${cosmos.node.rpc.endpoint:http://localhost:26657}")
    private String rpcEndpoint;

    @Value("${cosmos.node.faucet.endpoint:http://localhost:4500}")
    private String faucetEndpoint;

    @Value("${cosmos.chain.id:quicktest}")
    private String chainId;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CosmosBlockchainService cosmosBlockchainService(RestTemplate restTemplate) {
        return new CosmosBlockchainService(restTemplate, restEndpoint, rpcEndpoint, faucetEndpoint, chainId);
    }
}
