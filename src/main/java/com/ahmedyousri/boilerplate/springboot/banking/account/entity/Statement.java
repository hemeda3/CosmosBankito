package com.ahmedyousri.boilerplate.springboot.banking.account.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "statements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Statement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @Column(nullable = false)
    private String period;
    
    @Column(nullable = false)
    private String url;
    
    @Column(nullable = false)
    private LocalDateTime generatedAt;
    
    @Column(nullable = false)
    private LocalDateTime startDate;
    
    @Column(nullable = false)
    private LocalDateTime endDate;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal openingBalance;
    
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal closingBalance;
    
    @Column(nullable = false)
    private int transactionCount;
    
    private LocalDateTime downloadedAt;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
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
    
    public Account getAccount() {
        return account;
    }
    
    public String getPeriod() {
        return period;
    }
    
    public String getUrl() {
        return url;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }
    
    public BigDecimal getClosingBalance() {
        return closingBalance;
    }
    
    public int getTransactionCount() {
        return transactionCount;
    }
    
    public LocalDateTime getDownloadedAt() {
        return downloadedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Setter methods
    public void setId(UUID id) {
        this.id = id;
    }
    
    public void setAccount(Account account) {
        this.account = account;
    }
    
    public void setPeriod(String period) {
        this.period = period;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }
    
    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }
    
    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }
    
    public void setDownloadedAt(LocalDateTime downloadedAt) {
        this.downloadedAt = downloadedAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
