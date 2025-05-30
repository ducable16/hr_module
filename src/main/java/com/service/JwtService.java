package com.service;

import com.enums.Role;
import com.model.Employee;
import com.response.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${spring.key-gen}")
    private String SECRET_KEY;

    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 20;
    private final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7;

    // Generate token
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public TokenResponse generateTokenWithUserDetails(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof Employee) {
            claims.put("role", ((Employee)userDetails).getRole().name());
        }
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        return new TokenResponse(accessToken, refreshToken);
    }

    private Claims extractAllClaims(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        token = token.trim();
        try {
            return Jwts.parser()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (MalformedJwtException e) {
            System.out.println("‼ Malformed JWT: " + token);
            throw e;
        }
    }

    public String extractUsername(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        token = token.trim();
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Role extractRole(String token) {
        Claims claims = extractAllClaims(token);
        String roleStr = claims.get("role", String.class);
        return Role.valueOf(roleStr);
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(getSignKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public TokenResponse refreshToken(String refreshToken) {
        if (!isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Refresh token is invalid or expired");
        }

        Claims claims = extractAllClaims(refreshToken);
        String username = claims.getSubject();
        String roleStr = claims.get("role", String.class);
        Role role = Role.valueOf(roleStr);

        // Tạo access token mới
        Map<String, Object> newClaims = new HashMap<>();
        newClaims.put("role", role.name());

        String newAccessToken = Jwts.builder()
                .setClaims(newClaims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        // Có thể dùng lại refreshToken cũ, hoặc tạo refresh token mới tuỳ nhu cầu
        // Trong ví dụ này, tạo refresh token mới
//        String newRefreshToken = Jwts.builder()
//                .setClaims(newClaims)
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
//                .signWith(getSignKey(), SignatureAlgorithm.HS256)
//                .compact();

        return new TokenResponse(newAccessToken, refreshToken);
    }
}


