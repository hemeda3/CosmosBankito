package com.ahmedyousri.boilerplate.springboot.banking.validation.service;

import com.ahmedyousri.boilerplate.springboot.banking.exception.BusinessRuleException;
import org.apache.commons.validator.routines.IBANValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Service for validating banking data.
 * This service provides methods for validating account numbers, IBANs, currency codes, etc.
 */
@Service
public class ValidationService {
    
    private static final Logger log = LoggerFactory.getLogger(ValidationService.class);
    
    // Regular expression for validating account numbers
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^[A-Z0-9]{5,20}$");
    
    // Regular expression for validating email addresses
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    // Regular expression for validating phone numbers
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");
    
    // IBAN validator from Apache Commons Validator
    private static final IBANValidator IBAN_VALIDATOR = IBANValidator.getInstance();
    
    // Set of valid currency codes
    private static final Set<String> VALID_CURRENCY_CODES = new HashSet<>();
    
    // Initialize the set of valid currency codes
    static {
        for (Currency currency : Currency.getAvailableCurrencies()) {
            VALID_CURRENCY_CODES.add(currency.getCurrencyCode());
        }
    }
    
    /**
     * Validate an account number.
     *
     * @param accountNumber The account number to validate
     * @throws BusinessRuleException If the account number is invalid
     */
    public void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new BusinessRuleException("Account number cannot be empty", "INVALID_ACCOUNT_NUMBER");
        }
        
        if (!ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches()) {
            throw new BusinessRuleException(
                    "Invalid account number format. Account number must be 5-20 alphanumeric characters",
                    "INVALID_ACCOUNT_NUMBER_FORMAT"
            );
        }
        
        log.debug("Account number validated successfully: {}", accountNumber);
    }
    
    /**
     * Validate an IBAN.
     *
     * @param iban The IBAN to validate
     * @throws BusinessRuleException If the IBAN is invalid
     */
    public void validateIBAN(String iban) {
        if (iban == null || iban.isEmpty()) {
            throw new BusinessRuleException("IBAN cannot be empty", "INVALID_IBAN");
        }
        
        if (!IBAN_VALIDATOR.isValid(iban)) {
            throw new BusinessRuleException("Invalid IBAN format", "INVALID_IBAN_FORMAT");
        }
        
        log.debug("IBAN validated successfully: {}", iban);
    }
    
    /**
     * Validate a currency code.
     *
     * @param currencyCode The currency code to validate
     * @throws BusinessRuleException If the currency code is invalid
     */
    public void validateCurrencyCode(String currencyCode) {
        if (currencyCode == null || currencyCode.isEmpty()) {
            throw new BusinessRuleException("Currency code cannot be empty", "INVALID_CURRENCY_CODE");
        }
        
        if (!VALID_CURRENCY_CODES.contains(currencyCode)) {
            throw new BusinessRuleException("Invalid currency code: " + currencyCode, "INVALID_CURRENCY_CODE");
        }
        
        log.debug("Currency code validated successfully: {}", currencyCode);
    }
    
    /**
     * Validate an email address.
     *
     * @param email The email address to validate
     * @throws BusinessRuleException If the email address is invalid
     */
    public void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new BusinessRuleException("Email cannot be empty", "INVALID_EMAIL");
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessRuleException("Invalid email format", "INVALID_EMAIL_FORMAT");
        }
        
        log.debug("Email validated successfully: {}", email);
    }
    
    /**
     * Validate a phone number.
     *
     * @param phone The phone number to validate
     * @throws BusinessRuleException If the phone number is invalid
     */
    public void validatePhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            throw new BusinessRuleException("Phone number cannot be empty", "INVALID_PHONE");
        }
        
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessRuleException("Invalid phone number format", "INVALID_PHONE_FORMAT");
        }
        
        log.debug("Phone number validated successfully: {}", phone);
    }
    
    /**
     * Validate a name (first name or last name).
     *
     * @param name The name to validate
     * @throws BusinessRuleException If the name is invalid
     */
    public void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new BusinessRuleException("Name cannot be empty", "INVALID_NAME");
        }
        
        if (name.length() < 2 || name.length() > 50) {
            throw new BusinessRuleException("Name must be between 2 and 50 characters", "INVALID_NAME_LENGTH");
        }
        
        log.debug("Name validated successfully: {}", name);
    }
    
    /**
     * Validate an address.
     *
     * @param address The address to validate
     * @throws BusinessRuleException If the address is invalid
     */
    public void validateAddress(String address) {
        if (address == null || address.isEmpty()) {
            throw new BusinessRuleException("Address cannot be empty", "INVALID_ADDRESS");
        }
        
        if (address.length() < 5 || address.length() > 200) {
            throw new BusinessRuleException("Address must be between 5 and 200 characters", "INVALID_ADDRESS_LENGTH");
        }
        
        log.debug("Address validated successfully");
    }
    
    /**
     * Validate a postal code.
     *
     * @param postalCode The postal code to validate
     * @throws BusinessRuleException If the postal code is invalid
     */
    public void validatePostalCode(String postalCode) {
        if (postalCode == null || postalCode.isEmpty()) {
            throw new BusinessRuleException("Postal code cannot be empty", "INVALID_POSTAL_CODE");
        }
        
        if (postalCode.length() < 3 || postalCode.length() > 10) {
            throw new BusinessRuleException("Postal code must be between 3 and 10 characters", "INVALID_POSTAL_CODE_LENGTH");
        }
        
        log.debug("Postal code validated successfully: {}", postalCode);
    }
    
    /**
     * Validate a country code.
     *
     * @param countryCode The country code to validate
     * @throws BusinessRuleException If the country code is invalid
     */
    public void validateCountryCode(String countryCode) {
        if (countryCode == null || countryCode.isEmpty()) {
            throw new BusinessRuleException("Country code cannot be empty", "INVALID_COUNTRY_CODE");
        }
        
        if (countryCode.length() != 2) {
            throw new BusinessRuleException("Country code must be 2 characters", "INVALID_COUNTRY_CODE_LENGTH");
        }
        
        log.debug("Country code validated successfully: {}", countryCode);
    }
}
