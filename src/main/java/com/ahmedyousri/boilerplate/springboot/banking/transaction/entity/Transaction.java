package com.ahmedyousri.boilerplate.springboot.banking.transaction.entity;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a financial transaction.
 * Each transaction records a single debit or credit operation on an account.
 */
@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private String currencyCode;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceAfterTransaction;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    private String description;
    
    @Column(nullable = false)
    private String referenceId;
    
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
