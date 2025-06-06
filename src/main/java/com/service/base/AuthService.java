package com.service.base;

import com.request.LoginRequest;
import com.response.TokenResponse;

public interface AuthService {
    TokenResponse login(LoginRequest request);
    void logout(String refreshToken);
    TokenResponse refresh(String refreshToken);
}
