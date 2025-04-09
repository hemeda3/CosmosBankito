package com.ahmedyousri.boilerplate.springboot.banking.accounting.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a journal entry in the double-entry accounting system.
 * Each journal entry contains multiple entry lines that must balance (sum of debits = sum of credits).
 */
@Entity
@Table(name = "journal_entries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String reference;  // Reference to the original operation (e.g., transfer ID)
    
    @Column(nullable = false)
    private LocalDateTime entryDate;
    
    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<JournalEntryLine> entryLines = new ArrayList<>();
    
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (entryDate == null) {
            entryDate = LocalDateTime.now();
        }
    }
    
    /**
     * Helper method to add entry lines to this journal entry.
     * 
     * @param line The journal entry line to add
     */
    public void addEntryLine(JournalEntryLine line) {
        entryLines.add(line);
        line.setJournalEntry(this);
    }
    
    /**
     * Get the ID of this journal entry.
     * @return the ID
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * Set the ID of this journal entry.
     * @param id the ID
     */
    public void setId(UUID id) {
        this.id = id;
    }
    
    /**
     * Get the reference of this journal entry.
     * @return the reference
     */
    public String getReference() {
        return reference;
    }
    
    /**
     * Set the reference of this journal entry.
     * @param reference the reference
     */
    public void setReference(String reference) {
        this.reference = reference;
    }
    
    /**
     * Get the entry date of this journal entry.
     * @return the entry date
     */
    public LocalDateTime getEntryDate() {
        return entryDate;
    }
    
    /**
     * Set the entry date of this journal entry.
     * @param entryDate the entry date
     */
    public void setEntryDate(LocalDateTime entryDate) {
        this.entryDate = entryDate;
    }
    
    /**
     * Get the entry lines of this journal entry.
     * @return the entry lines
     */
    public List<JournalEntryLine> getEntryLines() {
        return entryLines;
    }
    
    /**
     * Set the entry lines of this journal entry.
     * @param entryLines the entry lines
     */
    public void setEntryLines(List<JournalEntryLine> entryLines) {
        this.entryLines = entryLines;
    }
    
    /**
     * Get the description of this journal entry.
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Set the description of this journal entry.
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Get the creation date of this journal entry.
     * @return the creation date
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Set the creation date of this journal entry.
     * @param createdAt the creation date
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
