package com.ahmedyousri.boilerplate.springboot.banking.customer.service;

import com.ahmedyousri.boilerplate.springboot.model.generated.CustomerContactRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.CustomerContactResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.CustomerContactUpdateRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.CustomerContactsResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.CustomerProfileResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.CustomerProfileUpdateRequest;
import com.ahmedyousri.boilerplate.springboot.model.generated.IdentificationDocumentResponse;
import com.ahmedyousri.boilerplate.springboot.model.generated.IdentificationDocumentUploadRequestFilesInner;
import com.ahmedyousri.boilerplate.springboot.model.generated.IdentificationDocumentsResponse;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CustomerService {
    
    /**
     * Get the current customer's profile
     * 
     * @return Customer profile details
     */
    CustomerProfileResponse getCurrentCustomerProfile();
    
    /**
     * Update the current customer's profile
     * 
     * @param request Profile update details
     * @return Updated customer profile
     */
    CustomerProfileResponse updateCustomerProfile(CustomerProfileUpdateRequest request);
    
    /**
     * Get the current customer's contact information
     * 
     * @return List of customer contacts
     */
    CustomerContactsResponse getCustomerContacts();
    
    /**
     * Add a new contact method for the customer
     * 
     * @param request Contact details to add
     * @return Added contact details
     */
    CustomerContactResponse addCustomerContact(CustomerContactRequest request);
    
    /**
     * Update an existing contact method
     * 
     * @param contactId ID of the contact to update
     * @param request Updated contact details
     * @return Updated contact details
     */
    CustomerContactResponse updateContact(UUID contactId, CustomerContactUpdateRequest request);
    
    /**
     * Delete a specific contact method
     * 
     * @param contactId ID of the contact to delete
     */
    void deleteContact(UUID contactId);
    
    /**
     * Get the customer's identification documents
     * 
     * @return List of identification documents
     */
    IdentificationDocumentsResponse getIdentificationDocuments();
    
    /**
     * Upload a new identification document
     * 
     * @param type Type of identification document
     * @param documentNumber Identification number
     * @param issuingCountry Country that issued the document
     * @param issueDate Date the document was issued
     * @param expiryDate Date the document expires
     * @param files Document files (front, back, selfie, etc.)
     * @param issuingAuthority Optional authority that issued the document
     * @return Details of the uploaded document
     */
    IdentificationDocumentResponse uploadIdentificationDocument(
        String type,
        String documentNumber,
        String issuingCountry,
        LocalDate issueDate,
        LocalDate expiryDate,
        List<IdentificationDocumentUploadRequestFilesInner> files,
        String issuingAuthority
    );
}
