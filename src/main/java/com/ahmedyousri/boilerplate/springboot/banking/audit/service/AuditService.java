package com.ahmedyousri.boilerplate.springboot.banking.audit.service;

import com.ahmedyousri.boilerplate.springboot.banking.audit.entity.AuditLog;
import com.ahmedyousri.boilerplate.springboot.banking.audit.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for audit logging.
 * This service provides methods for logging operations performed in the system.
 */
@Service
@RequiredArgsConstructor
public class AuditService {
    
    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    
    private final AuditRepository auditRepository;
    
    /**
     * Log a financial operation.
     *
     * @param operationType The type of operation
     * @param accountId     The ID of the account
     * @param amount        The amount involved in the operation
     * @param description   The description of the operation
     * @param username      The username of the user who performed the operation
     * @param successful    Whether the operation was successful
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFinancialOperation(
            String operationType,
            UUID accountId,
            BigDecimal amount,
            String description,
            String username,
            boolean successful
    ) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .timestamp(LocalDateTime.now())
                    .operationType(operationType)
                    .accountId(accountId)
                    .amount(amount)
                    .description(description)
                    .username(username)
                    .successful(successful)
                    .build();
            
            auditRepository.save(auditLog);
            
            log.debug("Audit log created for financial operation: {}", operationType);
        } catch (Exception e) {
            log.error("Error creating audit log for financial operation: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Log a financial operation with additional details.
     *
     * @param operationType The type of operation
     * @param accountId     The ID of the account
     * @param amount        The amount involved in the operation
     * @param description   The description of the operation
     * @param username      The username of the user who performed the operation
     * @param successful    Whether the operation was successful
     * @param details       Additional details about the operation
     * @param entityId      The ID of the entity involved in the operation
     * @param entityType    The type of entity involved in the operation
     * @param ipAddress     The IP address of the user who performed the operation
     * @param userAgent     The user agent of the user who performed the operation
     * @param errorMessage  The error message if the operation failed
     * @param referenceId   The reference ID of the operation
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFinancialOperationWithDetails(
            String operationType,
            UUID accountId,
            BigDecimal amount,
            String description,
            String username,
            boolean successful,
            String details,
            UUID entityId,
            String entityType,
            String ipAddress,
            String userAgent,
            String errorMessage,
            UUID referenceId
    ) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .timestamp(LocalDateTime.now())
                    .operationType(operationType)
                    .accountId(accountId)
                    .amount(amount)
                    .description(description)
                    .username(username)
                    .successful(successful)
                    .details(details)
                    .entityId(entityId)
                    .entityType(entityType)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .errorMessage(errorMessage)
                    .referenceId(referenceId)
                    .build();
            
            auditRepository.save(auditLog);
            
            log.debug("Audit log created for financial operation with details: {}", operationType);
        } catch (Exception e) {
            log.error("Error creating audit log for financial operation with details: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Log a system operation.
     *
     * @param operationType The type of operation
     * @param description   The description of the operation
     * @param username      The username of the user who performed the operation
     * @param successful    Whether the operation was successful
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSystemOperation(
            String operationType,
            String description,
            String username,
            boolean successful
    ) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .timestamp(LocalDateTime.now())
                    .operationType(operationType)
                    .description(description)
                    .username(username)
                    .successful(successful)
                    .build();
            
            auditRepository.save(auditLog);
            
            log.debug("Audit log created for system operation: {}", operationType);
        } catch (Exception e) {
            log.error("Error creating audit log for system operation: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Log a system operation with additional details.
     *
     * @param operationType The type of operation
     * @param description   The description of the operation
     * @param username      The username of the user who performed the operation
     * @param successful    Whether the operation was successful
     * @param details       Additional details about the operation
     * @param entityId      The ID of the entity involved in the operation
     * @param entityType    The type of entity involved in the operation
     * @param ipAddress     The IP address of the user who performed the operation
     * @param userAgent     The user agent of the user who performed the operation
     * @param errorMessage  The error message if the operation failed
     * @param referenceId   The reference ID of the operation
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSystemOperationWithDetails(
            String operationType,
            String description,
            String username,
            boolean successful,
            String details,
            UUID entityId,
            String entityType,
            String ipAddress,
            String userAgent,
            String errorMessage,
            UUID referenceId
    ) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .timestamp(LocalDateTime.now())
                    .operationType(operationType)
                    .description(description)
                    .username(username)
                    .successful(successful)
                    .details(details)
                    .entityId(entityId)
                    .entityType(entityType)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .errorMessage(errorMessage)
                    .referenceId(referenceId)
                    .build();
            
            auditRepository.save(auditLog);
            
            log.debug("Audit log created for system operation with details: {}", operationType);
        } catch (Exception e) {
            log.error("Error creating audit log for system operation with details: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Get audit logs by account ID.
     *
     * @param accountId The ID of the account
     * @param pageable  The pagination information
     * @return A page of audit logs
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByAccountId(UUID accountId, Pageable pageable) {
        return auditRepository.findByAccountId(accountId, pageable);
    }
    
    /**
     * Get audit logs by operation type.
     *
     * @param operationType The type of operation
     * @param pageable      The pagination information
     * @return A page of audit logs
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByOperationType(String operationType, Pageable pageable) {
        return auditRepository.findByOperationType(operationType, pageable);
    }
    
    /**
     * Get audit logs by username.
     *
     * @param username The username of the user who performed the operation
     * @param pageable The pagination information
     * @return A page of audit logs
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByUsername(String username, Pageable pageable) {
        return auditRepository.findByUsername(username, pageable);
    }
    
    /**
     * Get audit logs by success status.
     *
     * @param successful Whether the operation was successful
     * @param pageable   The pagination information
     * @return A page of audit logs
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsBySuccessful(boolean successful, Pageable pageable) {
        return auditRepository.findBySuccessful(successful, pageable);
    }
    
    /**
     * Get audit logs by timestamp range.
     *
     * @param startTime The start time of the range
     * @param endTime   The end time of the range
     * @param pageable  The pagination information
     * @return A page of audit logs
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByTimestampRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return auditRepository.findByTimestampBetween(startTime, endTime, pageable);
    }
    
    /**
     * Search audit logs.
     *
     * @param searchTerm The search term to match against various fields
     * @param pageable   The pagination information
     * @return A page of audit logs
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> searchAuditLogs(String searchTerm, Pageable pageable) {
        return auditRepository.search(searchTerm, pageable);
    }
}
