package com.ahmedyousri.boilerplate.springboot.banking.accounting.repository;

import com.ahmedyousri.boilerplate.springboot.banking.accounting.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for accessing JournalEntry entities.
 */
@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {
    
    /**
     * Find journal entries by reference.
     * 
     * @param reference The reference to search for
     * @return List of journal entries with the given reference
     */
    List<JournalEntry> findByReference(String reference);
    
    /**
     * Find journal entries within a date range.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return List of journal entries within the date range
     */
    List<JournalEntry> findByEntryDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find journal entries by reference and within a date range.
     * 
     * @param reference The reference to search for
     * @param startDate The start date
     * @param endDate The end date
     * @return List of journal entries with the given reference and within the date range
     */
    List<JournalEntry> findByReferenceAndEntryDateBetween(
            String reference, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find journal entries by description containing the given text.
     * 
     * @param description The description text to search for
     * @return List of journal entries with descriptions containing the given text
     */
    List<JournalEntry> findByDescriptionContaining(String description);
}
