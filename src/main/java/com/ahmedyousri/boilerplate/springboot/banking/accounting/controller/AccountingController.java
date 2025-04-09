package com.ahmedyousri.boilerplate.springboot.banking.accounting.controller;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.repository.AccountRepository;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.entity.JournalEntry;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.mapper.AccountingMapper;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.service.AccountingService;
import com.ahmedyousri.boilerplate.springboot.banking.customer.entity.Customer;
import com.ahmedyousri.boilerplate.springboot.banking.customer.service.CurrentCustomerService;
import com.ahmedyousri.boilerplate.springboot.banking.exception.BusinessRuleException;
import com.ahmedyousri.boilerplate.springboot.banking.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Controller for accessing accounting data.
 * This provides endpoints for viewing journal entries and account balances.
 */
@RestController
@RequestMapping("/api/v1/accounting")
@RequiredArgsConstructor
public class AccountingController {
    
    private final AccountingService accountingService;
    private final AccountRepository accountRepository;
    private final CurrentCustomerService currentCustomerService;
    private final AccountingMapper accountingMapper;
    
    /**
     * Get journal entries by reference.
     * 
     * @param reference The reference to search for
     * @return List of journal entries with the given reference
     */
    @GetMapping("/journal-entries")
    public ResponseEntity<Map<String, Object>> getJournalEntriesByReference(@RequestParam String reference) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        // Get journal entries by reference
        Iterable<JournalEntry> journalEntries = accountingService.getJournalEntriesByReference(reference);
        
        // Convert to list and filter to only include entries for the customer's accounts
        List<JournalEntry> filteredEntries = StreamSupport.stream(journalEntries.spliterator(), false)
                .filter(entry -> entry.getEntryLines().stream()
                        .anyMatch(line -> line.getAccount().getCustomer().getId().equals(customer.getId())))
                .collect(Collectors.toList());
        
        // Convert to DTOs
        List<AccountingMapper.JournalEntryDTO> journalEntryDTOs = accountingMapper.toJournalEntryDTOList(filteredEntries);
        
        // Create response
        Map<String, Object> response = new HashMap<>();
        response.put("reference", reference);
        response.put("journalEntries", journalEntryDTOs);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get a specific journal entry by ID.
     * 
     * @param journalEntryId The journal entry ID
     * @return The journal entry
     */
    @GetMapping("/journal-entries/{journalEntryId}")
    public ResponseEntity<AccountingMapper.JournalEntryDTO> getJournalEntryById(@PathVariable UUID journalEntryId) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        // Get journal entry by ID
        JournalEntry journalEntry = accountingService.getJournalEntryById(journalEntryId);
        
        // Check if the journal entry belongs to the customer
        boolean belongsToCustomer = journalEntry.getEntryLines().stream()
                .anyMatch(line -> line.getAccount().getCustomer().getId().equals(customer.getId()));
        
        if (!belongsToCustomer) {
            throw new BusinessRuleException(
                    "Journal entry does not belong to the authenticated customer", 
                    "JOURNAL_ENTRY_OWNERSHIP_VIOLATION");
        }
        
        // Convert to DTO
        AccountingMapper.JournalEntryDTO journalEntryDTO = accountingMapper.toJournalEntryDTO(journalEntry);
        
        return ResponseEntity.ok(journalEntryDTO);
    }
    
    /**
     * Get the account balance from journal entries.
     * 
     * @param accountId The account ID
     * @return The account balance
     */
    @GetMapping("/accounts/{accountId}/balance")
    public ResponseEntity<Map<String, Object>> getAccountBalanceFromJournalEntries(@PathVariable UUID accountId) {
        Customer customer = currentCustomerService.getCurrentCustomer();
        
        // Get account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));
        
        // Check if the account belongs to the customer
        if (!account.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessRuleException(
                    "Account does not belong to the authenticated customer", 
                    "ACCOUNT_OWNERSHIP_VIOLATION");
        }
        
        // Get account balance from journal entries
        java.math.BigDecimal balance = accountingService.getAccountBalanceFromJournalEntries(account);
        
        // Create response
        Map<String, Object> response = new HashMap<>();
        response.put("accountId", accountId);
        response.put("balance", balance);
        response.put("currencyCode", account.getCurrencyCode());
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
}
