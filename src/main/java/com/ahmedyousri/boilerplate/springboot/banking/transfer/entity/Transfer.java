package com.ahmedyousri.boilerplate.springboot.banking.transfer.entity;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;
    
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
    private TransferType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransferStatus status;
    
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime scheduledFor;
    
    private LocalDateTime completedAt;
    
    private String cancellationReason;
    
    private String referenceId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum TransferType {
        INTERNAL("internal"),
        EXTERNAL("external"),
        RECURRING("recurring"),
        SCHEDULED("scheduled"),
        COMPENSATION("compensation");
        
        private final String value;
        
        TransferType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static TransferType fromValue(String value) {
            for (TransferType type : TransferType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unexpected value: " + value);
        }
    }
    
    public enum TransferStatus {
        PENDING("pending"),
        PROCESSING("processing"),
        COMPLETED("completed"),
        FAILED("failed"),
        CANCELLED("cancelled"),
        SCHEDULED("scheduled"),
        COMPENSATED("compensated");
        
        private final String value;
        
        TransferStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static TransferStatus fromValue(String value) {
            for (TransferStatus status : TransferStatus.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unexpected value: " + value);
        }
    }
}
