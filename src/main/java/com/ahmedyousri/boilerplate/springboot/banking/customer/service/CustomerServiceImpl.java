package com.ahmedyousri.boilerplate.springboot.banking.customer.service;

import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.CustomerContact;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.IdentificationDocument;
import com.ahmedyousri.boilerplate.springboot.banking.customer.mapper.CustomerMapper;
import com.ahmedyousri.boilerplate.springboot.banking.customer.repository.CustomerContactRepository;
import com.ahmedyousri.boilerplate.springboot.banking.customer.repository.IdentificationDocumentRepository;
import com.ahmedyousri.boilerplate.springboot.banking.exception.BusinessRuleException;
import com.ahmedyousri.boilerplate.springboot.banking.exception.ResourceNotFoundException;
import com.ahmedyousri.boilerplate.springboot.model.generated.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private static final String UPLOAD_DIR = "/tmp/customer-documents/";
    
    private final CurrentCustomerService currentCustomerService;
    private final CustomerMapper customerMapper;
    private final CustomerContactRepository customerContactRepository;
    private final IdentificationDocumentRepository identificationDocumentRepository;
    
    @Override
    @Transactional(readOnly = true)
    public CustomerProfileResponse getCurrentCustomerProfile() {
        Customer customer = currentCustomerService.getCurrentCustomer();
        return customerMapper.toCustomerProfileResponse(customer);
    }
    
    @Override
    @Transactional
    public CustomerProfileResponse updateCustomerProfile(CustomerProfileUpdateRequest request) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        if (request.getFirstName() != null) {
            customer.setFirstName(request.getFirstName());
        }
        
        if (request.getLastName() != null) {
            customer.setLastName(request.getLastName());
        }
        
//        if (request.getPhone() != null) {
//            customer.setPhoneNumber(request.getPhone().getNumber());
//        }
        
        if (request.getPreferredLanguage() != null) {
            customer.setPreferredLanguage(request.getPreferredLanguage());
        }
        
        if (request.getCountryOfResidence() != null) {
            customer.setCountryOfResidence(request.getCountryOfResidence());
        }
        
        // Add more fields to update as needed
        
        log.info("Updated profile for customer: {}", customer.getId());
        
        return customerMapper.toCustomerProfileResponse(customer);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CustomerContactsResponse getCustomerContacts() {
        Customer customer = currentCustomerService.getCurrentCustomer();
        List<CustomerContact> contacts = customerContactRepository.findByCustomer(customer);
        
        CustomerContactsResponse response = new CustomerContactsResponse();
//        response.setContacts(customerMapper.toCustomerContactResponseList(contacts));
        
        return response;
    }
    
    @Override
    @Transactional
    public CustomerContactResponse addCustomerContact(CustomerContactRequest request) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        // Extract contact value from request
        String contactValue = getContactValueFromRequest(request);
        
        // Check if contact already exists
        if (customerContactRepository.existsByCustomerAndValue(customer, contactValue)) {
            throw new BusinessRuleException(
                    "Contact already exists", 
                    "CONTACT_ALREADY_EXISTS");
        }
        
        CustomerContact contact = customerMapper.toCustomerContact(request, customer);
        CustomerContact savedContact = customerContactRepository.save(contact);
        
        log.info("Added new contact for customer: {}", customer.getId());
        
        return customerMapper.toCustomerContactResponse(savedContact);
    }
    
    @Override
    @Transactional
    public CustomerContactResponse updateContact(UUID contactId, CustomerContactUpdateRequest request) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        CustomerContact contact = customerContactRepository.findByCustomerAndId(customer, contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", contactId));
        
        // Extract contact value from request based on type
        if (request.getPhone() != null) {
            contact.setValue(request.getPhone().getPhoneNumber());
        } else if (request.getEmail() != null) {
            contact.setValue(request.getEmail().getEmailAddress());
        } else if (request.getAddress() != null) {
            contact.setValue(request.getAddress().getAddressLine1());
        }
        
        if (request.getIsPrimary() != null) {
            contact.setPrimary(request.getIsPrimary());
        }
        
        CustomerContact updatedContact = customerContactRepository.save(contact);
        
        log.info("Updated contact {} for customer: {}", contactId, customer.getId());
        
        return customerMapper.toCustomerContactResponse(updatedContact);
    }
    
    @Override
    @Transactional
    public void deleteContact(UUID contactId) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        CustomerContact contact = customerContactRepository.findByCustomerAndId(customer, contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact", "id", contactId));
        
        customerContactRepository.delete(contact);
        
        log.info("Deleted contact {} for customer: {}", contactId, customer.getId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public IdentificationDocumentsResponse getIdentificationDocuments() {
        Customer customer = currentCustomerService.getCurrentCustomer();
        List<IdentificationDocument> documents = identificationDocumentRepository.findByCustomer(customer);
        
        IdentificationDocumentsResponse response = new IdentificationDocumentsResponse();
        response.setDocuments(customerMapper.toIdentificationDocumentResponseList(documents));
        
        return response;
    }
    
    @Override
    @Transactional
    public IdentificationDocumentResponse uploadIdentificationDocument(
            String type, 
            String documentNumber, 
            String issuingCountry, 
            LocalDate issueDate, 
            LocalDate expiryDate, 
            List<IdentificationDocumentUploadRequestFilesInner> files, 
            String issuingAuthority
    ) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        // Check if document already exists
        if (identificationDocumentRepository.existsByCustomerAndDocumentNumber(customer, documentNumber)) {
            throw new BusinessRuleException(
                    "Document already exists", 
                    "DOCUMENT_ALREADY_EXISTS");
        }
        
        // Save document files
        List<String> savedFilePaths = saveDocumentFiles(files);
        
        IdentificationDocument document = customerMapper.toIdentificationDocument(
                type, documentNumber, issuingCountry, issueDate, expiryDate, 
                files, issuingAuthority, customer);
        
        document.setDocumentFiles(savedFilePaths);
        
        IdentificationDocument savedDocument = identificationDocumentRepository.save(document);
        
        log.info("Uploaded identification document for customer: {}", customer.getId());
        
        return customerMapper.toIdentificationDocumentResponse(savedDocument);
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
     * Save uploaded document files to the filesystem
     */
    private List<String> saveDocumentFiles(List<IdentificationDocumentUploadRequestFilesInner> files) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            return files.stream().map(file -> {
                try {
                    // Handle the case where file.getFile() is a Resource instead of MultipartFile
                    String filename;
                    byte[] fileBytes;
                    
                    // Handle Resource objects
                    org.springframework.core.io.Resource resource = file.getFile();
                    
                    // Try to get a filename
                    String originalFilename = "file";
                    if (resource.getFilename() != null) {
                        originalFilename = resource.getFilename();
                    } else {
                        try {
                            if (resource.getURL() != null) {
                                String path = resource.getURL().getPath();
                                if (path.lastIndexOf('/') >= 0) {
                                    originalFilename = path.substring(path.lastIndexOf('/') + 1);
                                }
                            }
                        } catch (Exception e) {
                            // Ignore URL errors, use default filename
                        }
                    }
                    
                    filename = UUID.randomUUID().toString() + "_" + originalFilename;
                    
                    // Read the resource content
                    try {
                        fileBytes = org.springframework.util.StreamUtils.copyToByteArray(resource.getInputStream());
                    } catch (Exception e) {
                        throw new BusinessRuleException(
                            "Failed to process resource file: " + e.getMessage(), 
                            "RESOURCE_PROCESSING_ERROR");
                    }
                    
                    Path filePath = uploadPath.resolve(filename);
                    Files.write(filePath, fileBytes);
                    return filePath.toString();
                } catch (IOException e) {
                    throw new BusinessRuleException(
                            "Failed to save document file", 
                            "FILE_UPLOAD_ERROR");
                }
            }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new BusinessRuleException(
                    "Failed to create upload directory", 
                    "UPLOAD_DIRECTORY_ERROR");
        }
    }
}
