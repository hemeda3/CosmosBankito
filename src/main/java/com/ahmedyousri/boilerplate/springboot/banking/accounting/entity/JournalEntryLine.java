package com.ahmedyousri.boilerplate.springboot.banking.accounting.entity;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity representing a line in a journal entry.
 * Each line represents either a debit or credit to a specific account.
 */
@Entity
@Table(name = "journal_entry_lines")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryLine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    private JournalEntry journalEntry;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryType entryType;  // DEBIT or CREDIT
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private String currencyCode;
    
    private String description;
    
    /**
     * Enum representing the type of entry (debit or credit).
     */
    public enum EntryType {
        /**
         * Debit entry - typically represents an increase in assets or expenses,
         * or a decrease in liabilities, equity, or revenue.
         */
        DEBIT,
        
        /**
         * Credit entry - typically represents a decrease in assets or expenses,
         * or an increase in liabilities, equity, or revenue.
         */
        CREDIT
    }
    
    /**
     * Set the journal entry for this line.
     * @param journalEntry the journal entry
     */
    public void setJournalEntry(JournalEntry journalEntry) {
        this.journalEntry = journalEntry;
    }
    
    /**
     * Get the journal entry for this line.
     * @return the journal entry
     */
    public JournalEntry getJournalEntry() {
        return journalEntry;
    }
    
    /**
     * Get the account for this line.
     * @return the account
     */
    public Account getAccount() {
        return account;
    }
    
    /**
     * Set the account for this line.
     * @param account the account
     */
    public void setAccount(Account account) {
        this.account = account;
    }
    
    /**
     * Get the entry type for this line.
     * @return the entry type
     */
    public EntryType getEntryType() {
        return entryType;
    }
    
    /**
     * Set the entry type for this line.
     * @param entryType the entry type
     */
    public void setEntryType(EntryType entryType) {
        this.entryType = entryType;
    }
    
    /**
     * Get the amount for this line.
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }
    
    /**
     * Set the amount for this line.
     * @param amount the amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    /**
     * Get the currency code for this line.
     * @return the currency code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    /**
     * Set the currency code for this line.
     * @param currencyCode the currency code
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    
    /**
     * Get the description for this line.
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Set the description for this line.
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Get the ID of this journal entry line.
     * @return the ID
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * Set the ID of this journal entry line.
     * @param id the ID
     */
    public void setId(UUID id) {
        this.id = id;
    }
}
