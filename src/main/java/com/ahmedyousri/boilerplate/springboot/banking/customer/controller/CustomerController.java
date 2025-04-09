package com.ahmedyousri.boilerplate.springboot.banking.customer.controller;

import com.ahmedyousri.boilerplate.springboot.api.CustomersApi;
import com.ahmedyousri.boilerplate.springboot.banking.customer.service.CustomerService;
import com.ahmedyousri.boilerplate.springboot.model.generated.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {
    
    private final CustomerService customerService;
    
    @Override
    public ResponseEntity<CustomerContactResponse> _addCustomerContact(CustomerContactRequest customerContactRequest) {
        CustomerContactResponse response = customerService.addCustomerContact(customerContactRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Override
    public ResponseEntity<Void> _deleteContact(UUID contactId) {
        customerService.deleteContact(contactId);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<CustomerProfileResponse> _getCurrentCustomerProfile() {
        CustomerProfileResponse response = customerService.getCurrentCustomerProfile();
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<CustomerContactsResponse> _getCustomerContacts() {
        CustomerContactsResponse response = customerService.getCustomerContacts();
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<IdentificationDocumentsResponse> _getIdentificationDocuments() {
        IdentificationDocumentsResponse response = customerService.getIdentificationDocuments();
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<CustomerContactResponse> _updateContact(
            UUID contactId, 
            CustomerContactUpdateRequest customerContactUpdateRequest) {
        CustomerContactResponse response = customerService.updateContact(contactId, customerContactUpdateRequest);
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<CustomerProfileResponse> _updateCustomerProfile(
            CustomerProfileUpdateRequest customerProfileUpdateRequest) {
        CustomerProfileResponse response = customerService.updateCustomerProfile(customerProfileUpdateRequest);
        return ResponseEntity.ok(response);
    }
    
    @Override
    public ResponseEntity<IdentificationDocumentResponse> _uploadIdentificationDocument(
            String type, 
            String documentNumber, 
            String issuingCountry, 
            LocalDate issueDate, 
            LocalDate expiryDate, 
            List<IdentificationDocumentUploadRequestFilesInner> files, 
            String issuingAuthority) {
        
        IdentificationDocumentResponse response = customerService.uploadIdentificationDocument(
                type, documentNumber, issuingCountry, 
                issueDate, expiryDate, files, issuingAuthority);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
