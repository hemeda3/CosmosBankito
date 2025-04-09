package com.ahmedyousri.boilerplate.springboot.banking.transfer.entity;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "recurring_transfers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurringTransfer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;
    
    @Column(nullable = false)
    private String destinationAccountNumber;
    
    @Column(nullable = false)
    private String destinationBankCode;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private String currencyCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecurrenceFrequency frequency;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecurringTransferStatus status;
    
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime lastExecutedAt;
    
    private LocalDate lastExecutionDate;
    
    private LocalDateTime nextExecutionAt;
    
    private String cancellationReason;
    
    private LocalDate lastFailureDate;
    
    private String lastFailureReason;
    
    private Integer failureCount;
    
    private boolean active;
    
    private String deactivationReason;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        nextExecutionAt = startDate.atStartOfDay();
    }
    
    /**
     * Calculate the next execution date based on the frequency and last execution date.
     * This method updates the nextExecutionDate field.
     */
    public void calculateNextExecutionDate() {
        if (this.lastExecutionDate == null) {
            this.lastExecutionDate = LocalDate.now();
        }
        
        LocalDate nextDate;
        switch (this.frequency) {
            case DAILY:
                nextDate = this.lastExecutionDate.plusDays(1);
                break;
            case WEEKLY:
                nextDate = this.lastExecutionDate.plusWeeks(1);
                break;
            case BIWEEKLY:
                nextDate = this.lastExecutionDate.plusWeeks(2);
                break;
            case MONTHLY:
                nextDate = this.lastExecutionDate.plusMonths(1);
                break;
            case QUARTERLY:
                nextDate = this.lastExecutionDate.plusMonths(3);
                break;
            case ANNUALLY:
                nextDate = this.lastExecutionDate.plusYears(1);
                break;
            default:
                nextDate = this.lastExecutionDate.plusMonths(1);
        }
        
        this.nextExecutionAt = nextDate.atStartOfDay();
    }
    
    /**
     * Get the ID of this recurring transfer.
     * @return the ID
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * Set the ID of this recurring transfer.
     * @param id the ID
     */
    public void setId(UUID id) {
        this.id = id;
    }
    
    /**
     * Set the last execution date of this recurring transfer.
     * @param lastExecutionDate the last execution date
     */
    public void setLastExecutionDate(LocalDate lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }
    
    public enum RecurrenceFrequency {
        DAILY("daily"),
        WEEKLY("weekly"),
        BIWEEKLY("biweekly"),
        MONTHLY("monthly"),
        QUARTERLY("quarterly"),
        ANNUALLY("annually");
        
        private final String value;
        
        RecurrenceFrequency(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static RecurrenceFrequency fromValue(String value) {
            for (RecurrenceFrequency freq : RecurrenceFrequency.values()) {
                if (freq.value.equals(value)) {
                    return freq;
                }
            }
            throw new IllegalArgumentException("Unexpected value: " + value);
        }
    }
    
    public enum RecurringTransferStatus {
        ACTIVE("active"),
        PAUSED("paused"),
        COMPLETED("completed"),
        CANCELLED("cancelled");
        
        private final String value;
        
        RecurringTransferStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static RecurringTransferStatus fromValue(String value) {
            for (RecurringTransferStatus status : RecurringTransferStatus.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unexpected value: " + value);
        }
    }
}
