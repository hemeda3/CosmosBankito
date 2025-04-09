package com.ahmedyousri.boilerplate.springboot.banking.customer.service;

import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.CustomerStatus;
import com.ahmedyousri.boilerplate.springboot.banking.customer.repository.CustomerRepository;
import com.ahmedyousri.boilerplate.springboot.banking.exception.ResourceNotFoundException;
import com.ahmedyousri.boilerplate.springboot.model.User;
import com.ahmedyousri.boilerplate.springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CurrentCustomerServiceImpl implements CurrentCustomerService {
    
    private static final Logger log = LoggerFactory.getLogger(CurrentCustomerServiceImpl.class);
    
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    
    @Override
    public Customer getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }
        
        return customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user: " + username));
    }
    
    @Override
    @Transactional
    public Customer createCustomer(User user, String firstName, String lastName, String email, CustomerStatus status) {
        log.info("Creating new customer for user: {}", user.getUsername());
        
        // Check if customer already exists for this user
        customerRepository.findByUser(user).ifPresent(existingCustomer -> {
            log.warn("Customer already exists for user: {}", user.getUsername());
            throw new IllegalStateException("Customer already exists for user: " + user.getUsername());
        });
        
        // Create new customer
        Customer customer = Customer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .status(status)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        
        // Save and return the customer
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Created new customer with ID: {} for user: {}", savedCustomer.getId(), user.getUsername());
        
        return savedCustomer;
    }
    
    @Override
    public Customer getCustomerByUsername(String username) {
        log.debug("Looking up customer by username: {}", username);
        
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with username: " + username);
        }
        
        return customerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user: " + username));
    }
}
