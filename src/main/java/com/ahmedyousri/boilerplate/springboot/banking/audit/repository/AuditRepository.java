package com.ahmedyousri.boilerplate.springboot.banking.audit.repository;

import com.ahmedyousri.boilerplate.springboot.banking.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for audit log entries.
 * This repository provides methods for accessing audit log data.
 */
@Repository
public interface AuditRepository extends JpaRepository<AuditLog, UUID> {
    
    /**
     * Find audit logs by account ID.
     *
     * @param accountId The ID of the account
     * @param pageable  The pagination information
     * @return A page of audit logs
     */
    Page<AuditLog> findByAccountId(UUID accountId, Pageable pageable);
    
    /**
     * Find audit logs by operation type.
     *
     * @param operationType The type of operation
     * @param pageable      The pagination information
     * @return A page of audit logs
     */
    Page<AuditLog> findByOperationType(String operationType, Pageable pageable);
    
    /**
     * Find audit logs by username.
     *
     * @param username The username of the user who performed the operation
     * @param pageable The pagination information
     * @return A page of audit logs
     */
    Page<AuditLog> findByUsername(String username, Pageable pageable);
    
    /**
     * Find audit logs by success status.
     *
     * @param successful Whether the operation was successful
     * @param pageable   The pagination information
     * @return A page of audit logs
     */
    Page<AuditLog> findBySuccessful(boolean successful, Pageable pageable);
    
    /**
     * Find audit logs by timestamp range.
     *
     * @param startTime The start time of the range
     * @param endTime   The end time of the range
     * @param pageable  The pagination information
     * @return A page of audit logs
     */
    Page<AuditLog> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * Find audit logs by entity ID.
     *
     * @param entityId The ID of the entity
     * @param pageable The pagination information
     * @return A page of audit logs
     */
    Page<AuditLog> findByEntityId(UUID entityId, Pageable pageable);
    
    /**
     * Find audit logs by entity type.
     *
     * @param entityType The type of entity
     * @param pageable   The pagination information
     * @return A page of audit logs
     */
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);
    
    /**
     * Find audit logs by reference ID.
     *
     * @param referenceId The reference ID of the operation
     * @param pageable    The pagination information
     * @return A page of audit logs
     */
    Page<AuditLog> findByReferenceId(UUID referenceId, Pageable pageable);
    
    /**
     * Find audit logs by account ID and operation type.
     *
     * @param accountId     The ID of the account
     * @param operationType The type of operation
     * @param pageable      The pagination information
     * @return A page of audit logs
     */
    Page<AuditLog> findByAccountIdAndOperationType(UUID accountId, String operationType, Pageable pageable);
    
    /**
     * Find audit logs by account ID and success status.
     *
     * @param accountId  The ID of the account
     * @param successful Whether the operation was successful
     * @param pageable   The pagination information
     * @return A page of audit logs
     */
    Page<AuditLog> findByAccountIdAndSuccessful(UUID accountId, boolean successful, Pageable pageable);
    
    /**
     * Find audit logs by account ID and timestamp range.
     *
     * @param accountId The ID of the account
     * @param startTime The start time of the range
     * @param endTime   The end time of the range
     * @param pageable  The pagination information
     * @return A page of audit logs
     */
    Page<AuditLog> findByAccountIdAndTimestampBetween(UUID accountId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    
    /**
     * Find the most recent audit logs.
     *
     * @param limit The maximum number of audit logs to return
     * @return A list of audit logs
     */
    @Query("SELECT a FROM AuditLog a ORDER BY a.timestamp DESC")
    List<AuditLog> findMostRecent(@Param("limit") int limit, Pageable pageable);
    
    /**
     * Find audit logs by search criteria.
     *
     * @param searchTerm The search term to match against various fields
     * @param pageable   The pagination information
     * @return A page of audit logs
     */
    @Query("SELECT a FROM AuditLog a WHERE " +
            "a.operationType LIKE %:searchTerm% OR " +
            "a.description LIKE %:searchTerm% OR " +
            "a.username LIKE %:searchTerm% OR " +
            "a.details LIKE %:searchTerm% OR " +
            "a.entityType LIKE %:searchTerm% OR " +
            "a.errorMessage LIKE %:searchTerm%")
    Page<AuditLog> search(@Param("searchTerm") String searchTerm, Pageable pageable);
}
