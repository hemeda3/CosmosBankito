package com.ahmedyousri.boilerplate.springboot.banking.accounting.repository;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.entity.JournalEntry;
import com.ahmedyousri.boilerplate.springboot.banking.accounting.entity.JournalEntryLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Repository for accessing JournalEntryLine entities.
 */
@Repository
public interface JournalEntryLineRepository extends JpaRepository<JournalEntryLine, UUID> {
    
    /**
     * Find journal entry lines by account.
     * 
     * @param account The account to search for
     * @return List of journal entry lines for the given account
     */
    List<JournalEntryLine> findByAccount(Account account);
    
    /**
     * Find journal entry lines by journal entry.
     * 
     * @param journalEntry The journal entry to search for
     * @return List of journal entry lines for the given journal entry
     */
    List<JournalEntryLine> findByJournalEntry(JournalEntry journalEntry);
    
    /**
     * Find journal entry lines by account and entry type.
     * 
     * @param account The account to search for
     * @param entryType The entry type to search for
     * @return List of journal entry lines for the given account and entry type
     */
    List<JournalEntryLine> findByAccountAndEntryType(Account account, JournalEntryLine.EntryType entryType);
    
    /**
     * Calculate the sum of debit amounts for an account.
     * 
     * @param account The account to calculate for
     * @return The sum of debit amounts
     */
    @Query("SELECT SUM(jel.amount) FROM JournalEntryLine jel WHERE jel.account = ?1 AND jel.entryType = 'DEBIT'")
    BigDecimal sumDebitAmountsByAccount(Account account);
    
    /**
     * Calculate the sum of credit amounts for an account.
     * 
     * @param account The account to calculate for
     * @return The sum of credit amounts
     */
    @Query("SELECT SUM(jel.amount) FROM JournalEntryLine jel WHERE jel.account = ?1 AND jel.entryType = 'CREDIT'")
    BigDecimal sumCreditAmountsByAccount(Account account);
}
