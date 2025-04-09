package com.ahmedyousri.boilerplate.springboot.banking.customer.repository;

import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.CustomerStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testSaveAndRetrieveCustomer() {
        // Create a customer without setting ID
        Customer customer = Customer.builder()
                .firstName("Repository")
                .lastName("Test")
                .email("repo-test@example.com")
                .status(CustomerStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        // Save the customer
        Customer savedCustomer = customerRepository.save(customer);

        // Verify the customer was saved with an ID
        assertNotNull(savedCustomer.getId(), "Customer ID should not be null after save");

        // Retrieve the customer by ID
        Optional<Customer> retrievedCustomer = customerRepository.findById(savedCustomer.getId());
        
        // Verify the customer was retrieved
        assertTrue(retrievedCustomer.isPresent(), "Customer should be retrievable by ID");
        assertEquals("Repository", retrievedCustomer.get().getFirstName());
        assertEquals("Test", retrievedCustomer.get().getLastName());
        assertEquals("repo-test@example.com", retrievedCustomer.get().getEmail());
        assertEquals(CustomerStatus.ACTIVE, retrievedCustomer.get().getStatus());
    }

    @Test
    public void testFindByEmail() {
        // Create a customer
        Customer customer = Customer.builder()
                .firstName("Email")
                .lastName("Test")
                .email("email-test@example.com")
                .status(CustomerStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        // Save the customer
        customerRepository.save(customer);

        // Find the customer by email
        Optional<Customer> foundCustomer = customerRepository.findByEmail("email-test@example.com");
        
        // Verify the customer was found
        assertTrue(foundCustomer.isPresent(), "Customer should be retrievable by email");
        assertEquals("Email", foundCustomer.get().getFirstName());
        assertEquals("Test", foundCustomer.get().getLastName());
    }

    @Test
    public void testUpdateCustomer() {
        // Create a customer
        Customer customer = Customer.builder()
                .firstName("Update")
                .lastName("Test")
                .email("update-test@example.com")
                .status(CustomerStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        // Save the customer
        Customer savedCustomer = customerRepository.save(customer);
        
        // Update the customer
        savedCustomer.setFirstName("Updated");
        savedCustomer.setLastName("Name");
        Customer updatedCustomer = customerRepository.save(savedCustomer);
        
        // Verify the customer was updated
        assertEquals("Updated", updatedCustomer.getFirstName());
        assertEquals("Name", updatedCustomer.getLastName());
        
        // Retrieve the customer again to verify the update was persisted
        Optional<Customer> retrievedCustomer = customerRepository.findById(savedCustomer.getId());
        assertTrue(retrievedCustomer.isPresent());
        assertEquals("Updated", retrievedCustomer.get().getFirstName());
        assertEquals("Name", retrievedCustomer.get().getLastName());
    }
}
