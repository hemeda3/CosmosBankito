package com.ahmedyousri.boilerplate.springboot.banking.customer.entity;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true)
    private String name;
    
    private String type;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String phoneNumber;
    
    private LocalDate dateOfBirth;
    
    @Column(length = 2)
    private String preferredLanguage;
    
    @Column(length = 2)
    private String countryOfResidence;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CustomerStatus status = CustomerStatus.ACTIVE;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper method to add an account
    public void addAccount(Account account) {
        accounts.add(account);
        account.setCustomer(this);
    }
    
    // Helper method to remove an account
    public void removeAccount(Account account) {
        accounts.remove(account);
        account.setCustomer(null);
    }
    
    /**
     * Get the ID of this customer.
     * @return the ID
     */
    public UUID getId() {
        return id;
    }
    
    /**
     * Set the ID of this customer.
     * @param id the ID
     */
    public void setId(UUID id) {
        this.id = id;
    }
    
    /**
     * Get the first name of this customer.
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Set the first name of this customer.
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Get the last name of this customer.
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Set the last name of this customer.
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Get the email of this customer.
     * @return the email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Set the email of this customer.
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Get the status of this customer.
     * @return the status
     */
    public CustomerStatus getStatus() {
        return status;
    }
    
    /**
     * Set the status of this customer.
     * @param status the status
     */
    public void setStatus(CustomerStatus status) {
        this.status = status;
    }
    
    /**
     * Get the accounts of this customer.
     * @return the accounts
     */
    public List<Account> getAccounts() {
        return accounts;
    }
    
    /**
     * Set the accounts of this customer.
     * @param accounts the accounts
     */
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
