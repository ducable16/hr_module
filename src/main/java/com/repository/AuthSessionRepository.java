package com.repository;

import com.model.AuthSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {

    Optional<AuthSession> findByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);

}
