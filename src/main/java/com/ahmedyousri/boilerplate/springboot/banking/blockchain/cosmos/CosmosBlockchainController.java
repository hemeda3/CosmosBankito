package com.ahmedyousri.boilerplate.springboot.banking.blockchain.cosmos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * REST controller for Cosmos blockchain operations
 */
@RestController
@RequestMapping("/api/v1/blockchain/cosmos")
public class CosmosBlockchainController {

    private final CosmosBlockchainService cosmosBlockchainService;

    @Autowired
    public CosmosBlockchainController(CosmosBlockchainService cosmosBlockchainService) {
        this.cosmosBlockchainService = cosmosBlockchainService;
    }

    /**
     * Get blockchain status
     * @return Status information
     */
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok(cosmosBlockchainService.getStatus());
    }

    /**
     * Get account balance
     * @param address Account address
     * @return Balance information
     */
    @GetMapping("/balance/{address}")
    public ResponseEntity<String> getBalance(@PathVariable String address) {
        return ResponseEntity.ok(cosmosBlockchainService.getBalance(address));
    }

    /**
     * Send tokens from one account to another
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendTokens(@RequestBody TransferRequest request) {
        return ResponseEntity.ok(cosmosBlockchainService.sendTokens(
                request.getFromAddress(),
                request.getToAddress(),
                request.getAmount(),
                request.getDenom(),
                request.getPrivateKey()
        ));
    }

    /**
     * Get latest blocks
     * @param limit Number of blocks to retrieve (default: 10)
     * @return Block information
     */
    @GetMapping("/blocks")
    public ResponseEntity<String> getLatestBlocks(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(cosmosBlockchainService.getLatestBlocks(limit));
    }

    /**
     * Request model for token transfers
     */
    public static class TransferRequest {
        private String fromAddress;
        private String toAddress;
        private BigDecimal amount;
        private String denom;
        private String privateKey;

        // Getters and setters
        public String getFromAddress() {
            return fromAddress;
        }

        public void setFromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
        }

        public String getToAddress() {
            return toAddress;
        }

        public void setToAddress(String toAddress) {
            this.toAddress = toAddress;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getDenom() {
            return denom;
        }

        public void setDenom(String denom) {
            this.denom = denom;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }
    }
}
