package com.ahmedyousri.boilerplate.springboot.banking.accounting.service;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.entity.JournalEntry;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service for accounting operations.
 * Implements double-entry accounting principles where every transaction affects at least two accounts
 * and the sum of debits equals the sum of credits.
 */
public interface AccountingService {
    
    /**
     * Create a journal entry for a transfer between accounts.
     * 
     * @param sourceAccount The source account
     * @param destinationAccount The destination account
     * @param amount The transfer amount
     * @param description Description of the transfer
     * @param referenceId Reference to the original operation
     * @return Created journal entry
     */
    JournalEntry createTransferJournalEntry(
            Account sourceAccount, 
            Account destinationAccount, 
            BigDecimal amount, 
            String description,
            UUID referenceId);
    
    /**
     * Create a journal entry for a deposit.
     * 
     * @param account The account receiving the deposit
     * @param amount The deposit amount
     * @param description Description of the deposit
     * @param referenceId Reference to the original operation
     * @return Created journal entry
     */
    JournalEntry createDepositJournalEntry(
            Account account, 
            BigDecimal amount, 
            String description,
            UUID referenceId);
    
    /**
     * Create a journal entry for a withdrawal.
     * 
     * @param account The account for the withdrawal
     * @param amount The withdrawal amount
     * @param description Description of the withdrawal
     * @param referenceId Reference to the original operation
     * @return Created journal entry
     */
    JournalEntry createWithdrawalJournalEntry(
            Account account, 
            BigDecimal amount, 
            String description,
            UUID referenceId);
    
    /**
     * Validate that a journal entry is balanced (sum of debits equals sum of credits).
     * 
     * @param journalEntry The journal entry to validate
     * @return True if the journal entry is balanced
     */
    boolean validateJournalEntryBalance(JournalEntry journalEntry);
    
    /**
     * Get the account balance from journal entries.
     * This is calculated as the sum of credits minus the sum of debits.
     * 
     * @param account The account to get the balance for
     * @return The account balance
     */
    BigDecimal getAccountBalanceFromJournalEntries(Account account);
    
    /**
     * Get a journal entry by ID.
     * 
     * @param journalEntryId The journal entry ID
     * @return The journal entry
     */
    JournalEntry getJournalEntryById(UUID journalEntryId);
    
    /**
     * Get journal entries by reference.
     * 
     * @param reference The reference to search for
     * @return List of journal entries with the given reference
     */
    Iterable<JournalEntry> getJournalEntriesByReference(String reference);
    
    /**
     * Create a journal entry for a compensation transaction.
     * This is used to reverse a failed transaction.
     * 
     * @param account The account to compensate
     * @param amount The amount to compensate
     * @param description Description of the compensation
     * @param referenceId Reference to the original operation
     * @return Created journal entry
     */
    JournalEntry createCompensatingJournalEntry(
            Account account, 
            BigDecimal amount, 
            String description,
            UUID referenceId);
}
