package com.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TokenResponse {
    private String accessToken;
    private String refreshToken;

    public TokenResponse(String accessToken) {
        this.accessToken = accessToken;
        this.refreshToken = null;
    }
}
