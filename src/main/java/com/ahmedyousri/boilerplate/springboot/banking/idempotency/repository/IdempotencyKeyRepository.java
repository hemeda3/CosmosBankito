package com.ahmedyousri.boilerplate.springboot.banking.idempotency.repository;

import com.ahmedyousri.boilerplate.springboot.banking.idempotency.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing IdempotencyKey entities.
 */
@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, UUID> {
    
    /**
     * Find an idempotency key by its value and request path.
     * 
     * @param key The idempotency key value
     * @param requestPath The API endpoint path
     * @return Optional containing the idempotency key if found
     */
    Optional<IdempotencyKey> findByKeyAndRequestPath(String key, String requestPath);
    
    /**
     * Check if an idempotency key exists for the given value and request path.
     * 
     * @param key The idempotency key value
     * @param requestPath The API endpoint path
     * @return True if the key exists, false otherwise
     */
    boolean existsByKeyAndRequestPath(String key, String requestPath);
    
    /**
     * Delete all idempotency keys created before the specified timestamp.
     * Used for cleaning up expired keys.
     * 
     * @param timestamp The cutoff timestamp
     * @return The number of keys deleted
     */
    int deleteByCreatedAtBefore(LocalDateTime timestamp);
}
