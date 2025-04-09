package com.ahmedyousri.boilerplate.springboot.banking.account.repository;

import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Account;
import com.ahmedyousri.boilerplate.springboot.banking.account.entity.Statement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StatementRepository extends JpaRepository<Statement, UUID> {
    
    List<Statement> findByAccount(Account account);
    
    List<Statement> findByAccountOrderByGeneratedAtDesc(Account account);
    
    List<Statement> findByAccountAndGeneratedAtBetweenOrderByGeneratedAtDesc(
            Account account, LocalDateTime startDate, LocalDateTime endDate);
    
    Optional<Statement> findByAccountAndPeriod(Account account, String period);
}
