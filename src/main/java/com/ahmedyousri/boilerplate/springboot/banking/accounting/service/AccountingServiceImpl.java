package com.ahmedyousri.boilerplate.springboot.banking.accounting.service;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.entity.JournalEntry;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.entity.JournalEntryLine;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.repository.JournalEntryLineRepository;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.repository.JournalEntryRepository;
import com.ahmedyousri.boilerplate.springboot.banking.exception.BusinessRuleException;
import com.ahmedyousri.boilerplate.springboot.banking.system.service.SystemAccountService;
import com.ahmedyousri.boilerplate.springboot.banking.util.MoneyUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of the AccountingService interface.
 */
@Service
@RequiredArgsConstructor
public class AccountingServiceImpl implements AccountingService {
    
    private static final Logger log = LoggerFactory.getLogger(AccountingServiceImpl.class);
    
    private final JournalEntryRepository journalEntryRepository;
    private final JournalEntryLineRepository journalEntryLineRepository;
    private final SystemAccountService systemAccountService;
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public JournalEntry createTransferJournalEntry(
            Account sourceAccount, 
            Account destinationAccount, 
            BigDecimal amount, 
            String description,
            UUID referenceId) {
        
        // Create journal entry
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setReference(referenceId.toString());
        journalEntry.setDescription(description);
        journalEntry.setEntryDate(LocalDateTime.now());
        
        // Create debit entry line (source account decreases)
        JournalEntryLine debitLine = new JournalEntryLine();
        debitLine.setAccount(sourceAccount);
        debitLine.setEntryType(JournalEntryLine.EntryType.DEBIT);
        debitLine.setAmount(amount);
        debitLine.setCurrencyCode(sourceAccount.getCurrencyCode());
        debitLine.setDescription("Transfer out to " + destinationAccount.getAccountNumber());
        
        // Create credit entry line (destination account increases)
        JournalEntryLine creditLine = new JournalEntryLine();
        creditLine.setAccount(destinationAccount);
        creditLine.setEntryType(JournalEntryLine.EntryType.CREDIT);
        creditLine.setAmount(amount);
        creditLine.setCurrencyCode(destinationAccount.getCurrencyCode());
        creditLine.setDescription("Transfer in from " + sourceAccount.getAccountNumber());
        
        // Add lines to journal entry
        journalEntry.addEntryLine(debitLine);
        journalEntry.addEntryLine(creditLine);
        
        // Validate balance
        if (!validateJournalEntryBalance(journalEntry)) {
            throw new BusinessRuleException(
                    "Journal entry is not balanced", 
                    "UNBALANCED_JOURNAL_ENTRY");
        }
        
        // Save journal entry
        return journalEntryRepository.save(journalEntry);
    }
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public JournalEntry createDepositJournalEntry(
            Account account, 
            BigDecimal amount, 
            String description,
            UUID referenceId) {
        
        // Create journal entry
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setReference(referenceId.toString());
        journalEntry.setDescription(description);
        journalEntry.setEntryDate(LocalDateTime.now());
        
        // Create debit entry line (using a proper CASH account)
        JournalEntryLine debitLine = new JournalEntryLine();
        debitLine.setAccount(getCashAccount(account.getCurrencyCode())); // Get cash account for the correct currency
        debitLine.setEntryType(JournalEntryLine.EntryType.DEBIT);
        debitLine.setAmount(amount);
        debitLine.setCurrencyCode(account.getCurrencyCode());
        debitLine.setDescription("Cash deposit");
        
        // Create credit entry line (customer account increases)
        JournalEntryLine creditLine = new JournalEntryLine();
        creditLine.setAccount(account);
        creditLine.setEntryType(JournalEntryLine.EntryType.CREDIT);
        creditLine.setAmount(amount);
        creditLine.setCurrencyCode(account.getCurrencyCode());
        creditLine.setDescription("Deposit to account " + account.getAccountNumber());
        
        // Add lines to journal entry
        journalEntry.addEntryLine(debitLine);
        journalEntry.addEntryLine(creditLine);
        
        // Validate and save journal entry
        if (!validateJournalEntryBalance(journalEntry)) {
            throw new BusinessRuleException("Journal entry is not balanced", "UNBALANCED_JOURNAL_ENTRY");
        }
        
        return journalEntryRepository.save(journalEntry);
    }
    
    /**
     * Get a cash account for the specified currency.
     * 
     * @param currencyCode The currency code (e.g., USD, EUR)
     * @return The cash account for the specified currency
     */
    private Account getCashAccount(String currencyCode) {
        return systemAccountService.getCashAccount(currencyCode);
    }
    
    /**
     * Get a cash account with the default currency (USD).
     * 
     * @return The cash account with the default currency
     */
    private Account getCashAccount() {
        return getCashAccount("USD");
    }
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public JournalEntry createWithdrawalJournalEntry(
            Account account, 
            BigDecimal amount, 
            String description,
            UUID referenceId) {
        
        // Create journal entry
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setReference(referenceId.toString());
        journalEntry.setDescription(description);
        journalEntry.setEntryDate(LocalDateTime.now());
        
        // Create debit entry line (customer account decreases)
        JournalEntryLine debitLine = new JournalEntryLine();
        debitLine.setAccount(account);
        debitLine.setEntryType(JournalEntryLine.EntryType.DEBIT);
        debitLine.setAmount(amount);
        debitLine.setCurrencyCode(account.getCurrencyCode());
        debitLine.setDescription("Withdrawal from account " + account.getAccountNumber());
        
        // Create credit entry line (cash account increases)
        JournalEntryLine creditLine = new JournalEntryLine();
        creditLine.setAccount(getCashAccount(account.getCurrencyCode())); // Get cash account for the correct currency
        creditLine.setEntryType(JournalEntryLine.EntryType.CREDIT);
        creditLine.setAmount(amount);
        creditLine.setCurrencyCode(account.getCurrencyCode());
        creditLine.setDescription("Cash withdrawal");
        
        // Add lines to journal entry
        journalEntry.addEntryLine(debitLine);
        journalEntry.addEntryLine(creditLine);
        
        // Validate and save journal entry
        if (!validateJournalEntryBalance(journalEntry)) {
            throw new BusinessRuleException("Journal entry is not balanced", "UNBALANCED_JOURNAL_ENTRY");
        }
        
        return journalEntryRepository.save(journalEntry);
    }
    
    @Override
    public boolean validateJournalEntryBalance(JournalEntry journalEntry) {
        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;
        
        for (JournalEntryLine line : journalEntry.getEntryLines()) {
            if (line.getEntryType() == JournalEntryLine.EntryType.DEBIT) {
                totalDebits = MoneyUtil.add(totalDebits, line.getAmount());
            } else {
                totalCredits = MoneyUtil.add(totalCredits, line.getAmount());
            }
        }
        
        // Check if debits equal credits (zero difference)
        // Use MoneyUtil.round to ensure consistent precision for comparison
        return MoneyUtil.round(totalDebits).compareTo(MoneyUtil.round(totalCredits)) == 0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAccountBalanceFromJournalEntries(Account account) {
        BigDecimal totalDebits = journalEntryLineRepository.sumDebitAmountsByAccount(account);
        BigDecimal totalCredits = journalEntryLineRepository.sumCreditAmountsByAccount(account);
        
        // Handle null values (no entries)
        if (totalDebits == null) {
            totalDebits = BigDecimal.ZERO;
        }
        
        if (totalCredits == null) {
            totalCredits = BigDecimal.ZERO;
        }
        
        // For a typical asset account, credits increase the balance and debits decrease it
        return MoneyUtil.subtract(totalCredits, totalDebits);
    }
    
    @Override
    @Transactional(readOnly = true)
    public JournalEntry getJournalEntryById(UUID journalEntryId) {
        return journalEntryRepository.findById(journalEntryId)
                .orElseThrow(() -> new BusinessRuleException(
                        "Journal entry not found", 
                        "JOURNAL_ENTRY_NOT_FOUND"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Iterable<JournalEntry> getJournalEntriesByReference(String reference) {
        return journalEntryRepository.findByReference(reference);
    }
    
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public JournalEntry createCompensatingJournalEntry(
            Account account, 
            BigDecimal amount, 
            String description,
            UUID referenceId) {
        
        // Create journal entry
        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setReference(referenceId.toString());
        journalEntry.setDescription(description);
        journalEntry.setEntryDate(LocalDateTime.now());
        
        // For a compensation, we're essentially reversing a previous transaction
        // If the original transaction was a debit to the account (e.g., withdrawal or transfer out),
        // we need to credit the account to compensate
        
        // Create credit entry line (account increases)
        JournalEntryLine creditLine = new JournalEntryLine();
        creditLine.setAccount(account);
        creditLine.setEntryType(JournalEntryLine.EntryType.CREDIT);
        creditLine.setAmount(amount);
        creditLine.setCurrencyCode(account.getCurrencyCode());
        creditLine.setDescription("Compensation credit to account " + account.getAccountNumber());
        
        // Create debit entry line (cash account decreases)
        JournalEntryLine debitLine = new JournalEntryLine();
        debitLine.setAccount(getCashAccount(account.getCurrencyCode())); // Get cash account for the correct currency
        debitLine.setEntryType(JournalEntryLine.EntryType.DEBIT);
        debitLine.setAmount(amount);
        debitLine.setCurrencyCode(account.getCurrencyCode());
        debitLine.setDescription("Compensation debit from cash account");
        
        // Add lines to journal entry
        journalEntry.addEntryLine(creditLine);
        journalEntry.addEntryLine(debitLine);
        
        // Validate and save journal entry
        if (!validateJournalEntryBalance(journalEntry)) {
            throw new BusinessRuleException("Journal entry is not balanced", "UNBALANCED_JOURNAL_ENTRY");
        }
        
        return journalEntryRepository.save(journalEntry);
    }
}
