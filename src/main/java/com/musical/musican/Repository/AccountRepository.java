package com.musical.musican.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.musical.musican.Model.Entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByUsername(String username);

    Optional<Account> findByEmail(String email);

    List<Account> findByRole(Account.Role role);

    long countByActiveTrue();

    long countByRole(Account.Role role);

    @Query("SELECT a FROM Account a ORDER BY a.createdAt DESC")
    List<Account> findTop5ByOrderByCreatedAtDesc();
}
