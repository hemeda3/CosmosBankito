package com.ahmedyousri.boilerplate.springboot.banking.idempotency.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an idempotency key used to prevent duplicate request processing.
 * Idempotency keys are unique identifiers provided by clients to ensure that
 * operations are not accidentally performed multiple times.
 */
@Entity
@Table(name = "idempotency_keys")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyKey {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    /**
     * The idempotency key value provided by the client.
     * This should be unique for each distinct operation.
     */
    @Column(name = "key_value", nullable = false, unique = true)
    private String key;
    
    /**
     * The API endpoint path that the request was made to.
     * This allows the same idempotency key to be reused across different endpoints.
     */
    @Column(name = "request_path", nullable = false)
    private String requestPath;
    
    /**
     * The timestamp when the idempotency key was first used.
     * Used for expiration and cleanup.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * Optional response data that was returned for the original request.
     * This can be used to return the same response for duplicate requests.
     */
    @Column(name = "response_data", columnDefinition = "TEXT")
    private String responseData;
    
    /**
     * Optional status code that was returned for the original request.
     */
    @Column(name = "status_code")
    private Integer statusCode;
}
