package com.ahmedyousri.boilerplate.springboot.banking.customer.service;

import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.CustomerStatus;
import com.ahmedyousri.boilerplate.springboot.model.User;

/**
 * Service for retrieving and managing the current authenticated customer
 */
public interface CurrentCustomerService {
    
    /**
     * Get the current authenticated customer
     * 
     * @return The authenticated customer
     */
    Customer getCurrentCustomer();
    
    /**
     * Create a new customer for a user
     * 
     * @param user The user to create a customer for
     * @param firstName The customer's first name
     * @param lastName The customer's last name
     * @param email The customer's email
     * @param status The customer's status
     * @return The created customer
     */
    Customer createCustomer(User user, String firstName, String lastName, String email, CustomerStatus status);
    
    /**
     * Get a customer by username
     * 
     * @param username The username to look up
     * @return The customer associated with the username
     */
    Customer getCustomerByUsername(String username);
}
