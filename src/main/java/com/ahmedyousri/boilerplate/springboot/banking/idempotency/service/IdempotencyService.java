package com.ahmedyousri.boilerplate.springboot.banking.idempotency.service;

import com.ahmedyousri.boilerplate.springboot.banking.idempotency.entity.IdempotencyKey;
import com.ahmedyousri.boilerplate.springboot.banking.idempotency.repository.IdempotencyKeyRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for managing idempotency keys to prevent duplicate request processing.
 * This service provides functionality to check if a request with a given idempotency key
 * has already been processed, and to store the response for returning to duplicate requests.
 */
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyService.class);
    
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    
    /**
     * Checks if a request with the given key has already been processed.
     * If not, stores the key for future checks.
     * 
     * @param idempotencyKey The idempotency key value
     * @param requestPath The API endpoint path
     * @return true if this is the first request with this key (idempotent), false if it's a duplicate
     */
    @Transactional
    public boolean isIdempotent(String idempotencyKey, String requestPath) {
        // Check if the key exists
        if (idempotencyKeyRepository.existsByKeyAndRequestPath(idempotencyKey, requestPath)) {
            log.info("Duplicate request detected with idempotency key: {} for path: {}", idempotencyKey, requestPath);
            return false; // Not idempotent, request already processed
        }
        
        // Store the key
        IdempotencyKey key = new IdempotencyKey();
        key.setKey(idempotencyKey);
        key.setRequestPath(requestPath);
        key.setCreatedAt(LocalDateTime.now());
        idempotencyKeyRepository.save(key);
        
        log.debug("Stored new idempotency key: {} for path: {}", idempotencyKey, requestPath);
        return true; // Idempotent, this is the first time
    }
    
    /**
     * Stores the response for a request with the given idempotency key.
     * This allows returning the same response for duplicate requests.
     * 
     * @param idempotencyKey The idempotency key value
     * @param requestPath The API endpoint path
     * @param responseData The response data to store
     * @param statusCode The HTTP status code
     */
    @Transactional
    public void storeResponse(String idempotencyKey, String requestPath, String responseData, int statusCode) {
        Optional<IdempotencyKey> keyOptional = idempotencyKeyRepository.findByKeyAndRequestPath(idempotencyKey, requestPath);
        
        if (keyOptional.isPresent()) {
            IdempotencyKey key = keyOptional.get();
            key.setResponseData(responseData);
            key.setStatusCode(statusCode);
            idempotencyKeyRepository.save(key);
            log.debug("Stored response for idempotency key: {} for path: {}", idempotencyKey, requestPath);
        } else {
            log.warn("Attempted to store response for non-existent idempotency key: {} for path: {}", 
                    idempotencyKey, requestPath);
        }
    }
    
    /**
     * Retrieves the stored response for a request with the given idempotency key.
     * 
     * @param idempotencyKey The idempotency key value
     * @param requestPath The API endpoint path
     * @return Optional containing the stored response, or empty if not found
     */
    @Transactional(readOnly = true)
    public Optional<IdempotencyKey> getStoredResponse(String idempotencyKey, String requestPath) {
        return idempotencyKeyRepository.findByKeyAndRequestPath(idempotencyKey, requestPath);
    }
    
    /**
     * Cleans up expired idempotency keys.
     * This method is scheduled to run daily.
     */
    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    @Transactional
    public void cleanupExpiredKeys() {
        LocalDateTime expiryThreshold = LocalDateTime.now().minusDays(7); // Keys expire after 7 days
        int deleted = idempotencyKeyRepository.deleteByCreatedAtBefore(expiryThreshold);
        log.info("Cleaned up {} expired idempotency keys", deleted);
    }
}
