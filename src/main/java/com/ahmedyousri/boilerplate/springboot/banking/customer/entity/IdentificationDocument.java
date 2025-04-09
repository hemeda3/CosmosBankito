package com.ahmedyousri.boilerplate.springboot.banking.customer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "identification_documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentificationDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;
    
    @Column(nullable = false)
    private String documentNumber;
    
    @Column(nullable = false)
    private String issuingCountry;
    
    private String issuingAuthority;
    
    @Column(nullable = false)
    private LocalDate issueDate;
    
    @Column(nullable = false)
    private LocalDate expiryDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentVerificationStatus verificationStatus;
    
    @ElementCollection
    @CollectionTable(name = "identification_document_files", joinColumns = @JoinColumn(name = "document_id"))
    @Column(name = "file_path")
    private List<String> documentFiles;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        verificationStatus = DocumentVerificationStatus.PENDING;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum DocumentType {
        PASSPORT("passport"),
        NATIONAL_ID("nationalId"),
        DRIVER_LICENSE("driverLicense"),
        RESIDENCE_PERMIT("residencePermit");
        
        private final String value;
        
        DocumentType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static DocumentType fromValue(String value) {
            for (DocumentType type : DocumentType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unexpected value: " + value);
        }
    }
    
    public enum DocumentVerificationStatus {
        PENDING("pending"),
        VERIFIED("verified"),
        REJECTED("rejected");
        
        private final String value;
        
        DocumentVerificationStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static DocumentVerificationStatus fromValue(String value) {
            for (DocumentVerificationStatus status : DocumentVerificationStatus.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unexpected value: " + value);
        }
    }
}
