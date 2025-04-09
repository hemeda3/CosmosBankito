package com.ahmedyousri.boilerplate.springboot.banking.audit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an audit log entry.
 * This entity stores information about operations performed in the system.
 */
@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    /**
     * The unique identifier of the audit log entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    /**
     * The timestamp when the operation was performed.
     */
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    /**
     * The type of operation that was performed.
     */
    @Column(nullable = false)
    private String operationType;
    
    /**
     * The ID of the account involved in the operation.
     */
    private UUID accountId;
    
    /**
     * The amount involved in the operation.
     */
    @Column(precision = 19, scale = 4)
    private BigDecimal amount;
    
    /**
     * The description of the operation.
     */
    private String description;
    
    /**
     * The username of the user who performed the operation.
     */
    @Column(nullable = false)
    private String username;
    
    /**
     * Whether the operation was successful.
     */
    @Column(nullable = false)
    private boolean successful;
    
    /**
     * Additional details about the operation.
     */
    @Column(length = 1000)
    private String details;
    
    /**
     * The ID of the entity involved in the operation.
     */
    private UUID entityId;
    
    /**
     * The type of entity involved in the operation.
     */
    private String entityType;
    
    /**
     * The IP address of the user who performed the operation.
     */
    private String ipAddress;
    
    /**
     * The user agent of the user who performed the operation.
     */
    private String userAgent;
    
    /**
     * The error message if the operation failed.
     */
    private String errorMessage;
    
    /**
     * The reference ID of the operation.
     */
    private UUID referenceId;
    
    /**
     * Creates a new AuditLog builder.
     * @return a new AuditLog builder
     */
    public static AuditLogBuilder builder() {
        return new AuditLogBuilder();
    }
    
    /**
     * Builder class for AuditLog.
     */
    public static class AuditLogBuilder {
        private UUID id;
        private LocalDateTime timestamp;
        private String operationType;
        private UUID accountId;
        private BigDecimal amount;
        private String description;
        private String username;
        private boolean successful;
        private String details;
        private UUID entityId;
        private String entityType;
        private String ipAddress;
        private String userAgent;
        private String errorMessage;
        private UUID referenceId;
        
        AuditLogBuilder() {
        }
        
        public AuditLogBuilder id(UUID id) {
            this.id = id;
            return this;
        }
        
        public AuditLogBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public AuditLogBuilder operationType(String operationType) {
            this.operationType = operationType;
            return this;
        }
        
        public AuditLogBuilder accountId(UUID accountId) {
            this.accountId = accountId;
            return this;
        }
        
        public AuditLogBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }
        
        public AuditLogBuilder description(String description) {
            this.description = description;
            return this;
        }
        
        public AuditLogBuilder username(String username) {
            this.username = username;
            return this;
        }
        
        public AuditLogBuilder successful(boolean successful) {
            this.successful = successful;
            return this;
        }
        
        public AuditLogBuilder details(String details) {
            this.details = details;
            return this;
        }
        
        public AuditLogBuilder entityId(UUID entityId) {
            this.entityId = entityId;
            return this;
        }
        
        public AuditLogBuilder entityType(String entityType) {
            this.entityType = entityType;
            return this;
        }
        
        public AuditLogBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }
        
        public AuditLogBuilder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }
        
        public AuditLogBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }
        
        public AuditLogBuilder referenceId(UUID referenceId) {
            this.referenceId = referenceId;
            return this;
        }
        
        public AuditLog build() {
            return new AuditLog(id, timestamp, operationType, accountId, amount, description, username, successful, details, entityId, entityType, ipAddress, userAgent, errorMessage, referenceId);
        }
        
        public String toString() {
            return "AuditLog.AuditLogBuilder(id=" + this.id + ", timestamp=" + this.timestamp + ", operationType=" + this.operationType + ", accountId=" + this.accountId + ", amount=" + this.amount + ", description=" + this.description + ", username=" + this.username + ", successful=" + this.successful + ", details=" + this.details + ", entityId=" + this.entityId + ", entityType=" + this.entityType + ", ipAddress=" + this.ipAddress + ", userAgent=" + this.userAgent + ", errorMessage=" + this.errorMessage + ", referenceId=" + this.referenceId + ")";
        }
    }
}
