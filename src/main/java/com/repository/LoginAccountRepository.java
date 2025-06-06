package com.repository;


import com.model.LoginAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginAccountRepository extends JpaRepository<LoginAccount, Long> {

    Optional<LoginAccount> findByEmail(String email);

    Optional<LoginAccount> findLoginAccountByEmployeeId(Long employeeId);

    boolean existsByEmail(String email);
}

