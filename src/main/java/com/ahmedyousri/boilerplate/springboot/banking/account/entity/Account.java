package com.ahmedyousri.boilerplate.springboot.banking.account.entity;

import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@lombok.Getter
@lombok.Setter
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Version
    private Long version;
    
    @Column(unique = true, nullable = false)
    private String accountNumber;
    
    @Column(unique = true)
    private String blockchainAccountId;
    
    private String iban;
    
    private String accountName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;
    
    @Column(nullable = false)
    private String currencyCode;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal currentBalance;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal availableBalance;
    
    @Column(nullable = false)
    private LocalDateTime openedDate;
    
    private LocalDateTime lastTransactionDate;
    
    private Double interestRate;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime closedAt;
    
    private String closureReason;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (openedDate == null) {
            openedDate = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getter methods
    public UUID getId() {
        return id;
    }
    
    public Long getVersion() {
        return version;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public String getBlockchainAccountId() {
        return blockchainAccountId;
    }
    
    public String getIban() {
        return iban;
    }
    
    public String getAccountName() {
        return accountName;
    }
    
    public AccountType getType() {
        return type;
    }
    
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    public AccountStatus getStatus() {
        return status;
    }
    
    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }
    
    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }
    
    public LocalDateTime getOpenedDate() {
        return openedDate;
    }
    
    public LocalDateTime getLastTransactionDate() {
        return lastTransactionDate;
    }
    
    public Double getInterestRate() {
        return interestRate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public LocalDateTime getClosedAt() {
        return closedAt;
    }
    
    public String getClosureReason() {
        return closureReason;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    // Setter methods - don't provide a setter for ID to prevent manual ID assignment
    // This forces the application to let Hibernate manage the ID
    
    // No setter for version field - it's managed by Hibernate
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public void setBlockchainAccountId(String blockchainAccountId) {
        this.blockchainAccountId = blockchainAccountId;
    }
    
    public void setIban(String iban) {
        this.iban = iban;
    }
    
    public void setAccountName(@Size(max=100) String accountName) {
        this.accountName = accountName;
    }
    
    public void setType(AccountType type) {
        this.type = type;
    }
    
    public void setCurrencyCode(@NotNull @Size(min=3, max=3) String currencyCode) {
        this.currencyCode = currencyCode;
    }
    
    public void setStatus(AccountStatus status) {
        this.status = status;
    }
    
    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }
    
    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }
    
    public void setOpenedDate(LocalDateTime openedDate) {
        this.openedDate = openedDate;
    }
    
    public void setLastTransactionDate(LocalDateTime lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }
    
    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }
    
    public void setClosureReason(String closureReason) {
        this.closureReason = closureReason;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
