package com.ahmedyousri.boilerplate.springboot.banking.accounting.mapper;

import com.ahmedyousri.boilerplate.springboot.banking.accounting.entity.JournalEntry;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.entity.JournalEntryLine;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for accounting entities.
 * Converts accounting entities to DTOs for API responses.
 */
@Component
public class AccountingMapper {
    
    /**
     * Convert a JournalEntry to a JournalEntryDTO.
     * 
     * @param journalEntry The journal entry to convert
     * @return The journal entry DTO
     */
    public JournalEntryDTO toJournalEntryDTO(JournalEntry journalEntry) {
        if (journalEntry == null) {
            return null;
        }
        
        JournalEntryDTO dto = new JournalEntryDTO();
        dto.setId(journalEntry.getId());
        dto.setReference(journalEntry.getReference());
        dto.setDescription(journalEntry.getDescription());
        dto.setEntryDate(toOffsetDateTime(journalEntry.getEntryDate()));
        
        // Convert entry lines
        List<JournalEntryLineDTO> entryLines = journalEntry.getEntryLines().stream()
                .map(this::toJournalEntryLineDTO)
                .collect(Collectors.toList());
        dto.setEntryLines(entryLines);
        
        return dto;
    }
    
    /**
     * Convert a JournalEntryLine to a JournalEntryLineDTO.
     * 
     * @param journalEntryLine The journal entry line to convert
     * @return The journal entry line DTO
     */
    public JournalEntryLineDTO toJournalEntryLineDTO(JournalEntryLine journalEntryLine) {
        if (journalEntryLine == null) {
            return null;
        }
        
        JournalEntryLineDTO dto = new JournalEntryLineDTO();
        dto.setId(journalEntryLine.getId());
        dto.setAccountId(journalEntryLine.getAccount().getId());
        dto.setAccountNumber(journalEntryLine.getAccount().getAccountNumber());
        dto.setEntryType(journalEntryLine.getEntryType().name());
        dto.setAmount(journalEntryLine.getAmount());
        dto.setCurrencyCode(journalEntryLine.getCurrencyCode());
        dto.setDescription(journalEntryLine.getDescription());
        
        return dto;
    }
    
    /**
     * Convert a list of JournalEntry to a list of JournalEntryDTO.
     * 
     * @param journalEntries The journal entries to convert
     * @return The journal entry DTOs
     */
    public List<JournalEntryDTO> toJournalEntryDTOList(List<JournalEntry> journalEntries) {
        if (journalEntries == null) {
            return null;
        }
        
        return journalEntries.stream()
                .map(this::toJournalEntryDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert LocalDateTime to OffsetDateTime.
     * 
     * @param localDateTime The local date time to convert
     * @return The offset date time
     */
    private OffsetDateTime toOffsetDateTime(java.time.LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }
    
    /**
     * DTO for JournalEntry.
     */
    public static class JournalEntryDTO {
        private java.util.UUID id;
        private String reference;
        private String description;
        private OffsetDateTime entryDate;
        private List<JournalEntryLineDTO> entryLines;
        
        // Getters and setters
        
        public java.util.UUID getId() {
            return id;
        }
        
        public void setId(java.util.UUID id) {
            this.id = id;
        }
        
        public String getReference() {
            return reference;
        }
        
        public void setReference(String reference) {
            this.reference = reference;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public OffsetDateTime getEntryDate() {
            return entryDate;
        }
        
        public void setEntryDate(OffsetDateTime entryDate) {
            this.entryDate = entryDate;
        }
        
        public List<JournalEntryLineDTO> getEntryLines() {
            return entryLines;
        }
        
        public void setEntryLines(List<JournalEntryLineDTO> entryLines) {
            this.entryLines = entryLines;
        }
    }
    
    /**
     * DTO for JournalEntryLine.
     */
    public static class JournalEntryLineDTO {
        private java.util.UUID id;
        private java.util.UUID accountId;
        private String accountNumber;
        private String entryType;
        private java.math.BigDecimal amount;
        private String currencyCode;
        private String description;
        
        // Getters and setters
        
        public java.util.UUID getId() {
            return id;
        }
        
        public void setId(java.util.UUID id) {
            this.id = id;
        }
        
        public java.util.UUID getAccountId() {
            return accountId;
        }
        
        public void setAccountId(java.util.UUID accountId) {
            this.accountId = accountId;
        }
        
        public String getAccountNumber() {
            return accountNumber;
        }
        
        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }
        
        public String getEntryType() {
            return entryType;
        }
        
        public void setEntryType(String entryType) {
            this.entryType = entryType;
        }
        
        public java.math.BigDecimal getAmount() {
            return amount;
        }
        
        public void setAmount(java.math.BigDecimal amount) {
            this.amount = amount;
        }
        
        public String getCurrencyCode() {
            return currencyCode;
        }
        
        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
    }
}
