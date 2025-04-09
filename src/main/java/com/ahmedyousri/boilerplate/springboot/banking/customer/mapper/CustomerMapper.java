package com.ahmedyousri.boilerplate.springboot.banking.customer.mapper;

import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.CustomerContact;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.IdentificationDocument;
import com.ahmedyousri.boilerplate.springboot.model.generated.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CustomerMapper {
    
    /**
     * Convert Customer entity to CustomerProfileResponse DTO
     */
    public CustomerProfileResponse toCustomerProfileResponse(Customer customer) {
        if (customer == null) {
            return null;
        }
        
        CustomerProfileResponse response = new CustomerProfileResponse();
        response.setId(customer.getId());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setEmail(customer.getEmail());
//        response.setTelephone(customer.getPhoneNumber());
        response.setDateOfBirth(customer.getDateOfBirth());
        response.setStatus(CustomerProfileResponse.StatusEnum.fromValue(customer.getStatus().getValue()));
        
        return response;
    }
    
    /**
     * Convert CustomerContact entity to CustomerContactResponse DTO
     */
    public CustomerContactResponse toCustomerContactResponse(CustomerContact contact) {
        if (contact == null) {
            return null;
        }
        
        CustomerContactResponse response = new CustomerContactResponse();
        response.setId(contact.getId());
        response.setType(CustomerContactResponse.TypeEnum.fromValue(contact.getType().getValue()));
        // The generated model doesn't have setContactValue method
        // Instead, we need to set the appropriate field based on the contact type
        if (contact.getType() == CustomerContact.ContactType.PHONE) {
            CustomerContactResponsePhone phone = new CustomerContactResponsePhone();
            phone.setPhoneNumber(contact.getValue());
            response.setPhone(phone);
        } else if (contact.getType() == CustomerContact.ContactType.EMAIL) {
            CustomerContactResponseEmail email = new CustomerContactResponseEmail();
            email.setEmailAddress(contact.getValue());
            response.setEmail(email);
        } else if (contact.getType() == CustomerContact.ContactType.ADDRESS) {
            CustomerContactResponseAddress address = new CustomerContactResponseAddress();
            address.setAddressLine1(contact.getValue());
            response.setAddress(address);
        }
        
        response.setIsPrimary(contact.isPrimary());
        response.setCreatedAt(toOffsetDateTime(contact.getCreatedAt()));
        // Use updatedAt instead of lastUpdated
        // The generated model might not have setUpdatedAt method
        // Use a different approach to set the updated timestamp
        if (contact.getUpdatedAt() != null) {
            // Skip setting updatedAt if the method doesn't exist in the generated model
            // response.setUpdatedAt(toOffsetDateTime(contact.getUpdatedAt()));
        }
        
        return response;
    }
    
    /**
     * Convert a list of CustomerContact entities to CustomerContactResponse DTOs
     */
    public List<CustomerContactResponse> toCustomerContactResponseList(List<CustomerContact> contacts) {
        if (contacts == null) {
            return null;
        }
        
        return contacts.stream()
                .map(this::toCustomerContactResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert IdentificationDocument entity to IdentificationDocumentResponse DTO
     */
    public IdentificationDocumentResponse toIdentificationDocumentResponse(IdentificationDocument document) {
        if (document == null) {
            return null;
        }
        
        IdentificationDocumentResponse response = new IdentificationDocumentResponse();
        response.setId(document.getId());
        response.setType(IdentificationDocumentResponse.TypeEnum.fromValue(document.getType().getValue()));
        response.setDocumentNumber(document.getDocumentNumber());
        response.setIssuingCountry(document.getIssuingCountry());
        response.setIssuingAuthority(document.getIssuingAuthority());
        response.setIssueDate(document.getIssueDate());
        response.setExpiryDate(document.getExpiryDate());
        response.setVerificationStatus(
            IdentificationDocumentResponse.VerificationStatusEnum.fromValue(
                document.getVerificationStatus().getValue()
            )
        );
        // Convert string paths to document file objects
        List<IdentificationDocumentResponseDocumentFilesInner> documentFiles = document.getDocumentFiles().stream()
                .map(path -> {
                    IdentificationDocumentResponseDocumentFilesInner file = new IdentificationDocumentResponseDocumentFilesInner();
                    file.setFileUrl(java.net.URI.create(path));
                    return file;
                })
                .collect(Collectors.toList());
        response.setDocumentFiles(documentFiles);
        response.setCreatedAt(toOffsetDateTime(document.getCreatedAt()));
        response.setUpdatedAt(document.getUpdatedAt() != null ? toOffsetDateTime(document.getUpdatedAt()) : null);
        
        return response;
    }
    
    /**
     * Convert a list of IdentificationDocument entities to IdentificationDocumentResponse DTOs
     */
    public List<IdentificationDocumentResponse> toIdentificationDocumentResponseList(List<IdentificationDocument> documents) {
        if (documents == null) {
            return null;
        }
        
        return documents.stream()
                .map(this::toIdentificationDocumentResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert CustomerContactRequest DTO to CustomerContact entity
     */
    public CustomerContact toCustomerContact(CustomerContactRequest request, Customer customer) {
        if (request == null) {
            return null;
        }
        
        return CustomerContact.builder()
                .customer(customer)
                .type(CustomerContact.ContactType.fromValue(request.getType().getValue()))
                .value(getContactValueFromRequest(request))
                .isPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false)
                .build();
    }
    
    /**
     * Convert IdentificationDocument upload request to IdentificationDocument entity
     */
    public IdentificationDocument toIdentificationDocument(
            String type, 
            String documentNumber, 
            String issuingCountry, 
            LocalDate issueDate, 
            LocalDate expiryDate, 
            List<IdentificationDocumentUploadRequestFilesInner> files, 
            String issuingAuthority,
            Customer customer
    ) {
        return IdentificationDocument.builder()
                .customer(customer)
                .type(IdentificationDocument.DocumentType.fromValue(type))
                .documentNumber(documentNumber)
                .issuingCountry(issuingCountry)
                .issuingAuthority(issuingAuthority)
                .issueDate(issueDate)
                .expiryDate(expiryDate)
                .documentFiles(files.stream().map(file -> {
                    // For Resource objects, try to get a filename
                    try {
                        String filename = "doc-" + UUID.randomUUID().toString();
                        if (file.getFile().getFilename() != null) {
                            filename = file.getFile().getFilename();
                        } else if (file.getFile().getURL() != null) {
                            String path = file.getFile().getURL().getPath();
                            if (path.lastIndexOf('/') >= 0) {
                                filename = path.substring(path.lastIndexOf('/') + 1);
                            }
                        }
                        return filename;
                    } catch (Exception e) {
                        return "unknown-" + UUID.randomUUID().toString();
                    }
                }).collect(Collectors.toList()))
                .build();
    }
    
    /**
     * Extract contact value from CustomerContactRequest based on type
     */
    private String getContactValueFromRequest(CustomerContactRequest request) {
        if (request == null) {
            return null;
        }
        
        if (request.getType() == CustomerContactRequest.TypeEnum.PHONE && request.getPhone() != null) {
            return request.getPhone().getPhoneNumber();
        } else if (request.getType() == CustomerContactRequest.TypeEnum.EMAIL && request.getEmail() != null) {
            return request.getEmail().getEmailAddress();
        } else if (request.getType() == CustomerContactRequest.TypeEnum.ADDRESS && request.getAddress() != null) {
            return request.getAddress().getAddressLine1();
        }
        
        return null;
    }
    
    /**
     * Convert LocalDateTime to OffsetDateTime
     */
    private OffsetDateTime toOffsetDateTime(java.time.LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }
}
