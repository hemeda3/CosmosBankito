package com.ahmedyousri.boilerplate.springboot.banking.customer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer_contacts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerContact {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContactType type;
    
    @Column(name = "contact_value", nullable = false)
    private String value;
    
    private boolean isPrimary;
    
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
    
    public enum ContactType {
        PHONE("phone"),
        EMAIL("email"),
        ADDRESS("address");
        
        private final String value;
        
        ContactType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static ContactType fromValue(String value) {
            for (ContactType type : ContactType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unexpected value: " + value);
        }
    }
}
