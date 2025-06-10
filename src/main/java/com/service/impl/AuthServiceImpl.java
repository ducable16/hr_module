package com.service.impl;

import com.exception.EntityNotFoundException;
import com.model.AuthSession;
import com.model.LoginAccount;
import com.repository.AuthSessionRepository;
import com.repository.LoginAccountRepository;
import com.request.LoginRequest;
import com.request.RefreshTokenRequest;
import com.response.TokenResponse;
import com.service.JwtService;
import com.service.base.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.exception.IllegalArgumentException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final LoginAccountRepository loginAccountRepository;
    private final JwtService jwtService;
    private final AuthSessionRepository authSessionRepository;

    @Override
    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        LoginAccount account = loginAccountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        TokenResponse tokenResponse = jwtService.generateTokenWithUserDetails(account);

        AuthSession session = AuthSession.builder()
                .employeeId(account.getEmployeeId())
                .refreshToken(tokenResponse.getRefreshToken())
                .expiresAt(jwtService.extractExpiration(tokenResponse.getRefreshToken()))
                .build();

        authSessionRepository.save(session);
        return tokenResponse;
    }

    @Override
    public void logout(String refreshToken) {
        authSessionRepository.deleteByRefreshToken(refreshToken);
    }

    @Override
    public TokenResponse refresh(RefreshTokenRequest request) {
        if (!jwtService.isTokenValid(request.getRefreshToken())) {
            System.out.println(request.getRefreshToken());
            throw new IllegalArgumentException("Refresh token is invalid or expired");
        }

        AuthSession session = authSessionRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            authSessionRepository.delete(session);
            throw new IllegalArgumentException("Refresh token expired");
        }

        LoginAccount account = loginAccountRepository.findByEmail(
                jwtService.extractUsername(request.getRefreshToken())
        ).orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Tạo token mới
        TokenResponse newToken = jwtService.generateTokenWithUserDetails(account);

        // Cập nhật refreshToken
        session.setRefreshToken(newToken.getRefreshToken());
        session.setExpiresAt(jwtService.extractExpiration(newToken.getRefreshToken()));
        authSessionRepository.save(session);

        return newToken;
    }
}
