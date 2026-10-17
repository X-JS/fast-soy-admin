package com.fastsoy.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-expire-minutes:30}")
    private long accessExpireMinutes;

    @Value("${app.jwt.refresh-expire-minutes:720}")
    private long refreshExpireMinutes;

    private SecretKey getSecretKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Long userId, Integer tokenVersion) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("tokenType", "accessToken")
                .claim("tokenVersion", tokenVersion)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpireMinutes * 60 * 1000))
                .signWith(getSecretKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(Long userId, Integer tokenVersion) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("tokenType", "refreshToken")
                .claim("tokenVersion", tokenVersion)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpireMinutes * 60 * 1000))
                .signWith(getSecretKey(), Jwts.SIG.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .requireAudience("fastsoy")
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(parseToken(token).getSubject());
    }

    public Integer getTokenVersionFromToken(String token) {
        return parseToken(token).get("tokenVersion", Integer.class);
    }

    public String getTokenTypeFromToken(String token) {
        return parseToken(token).get("tokenType", String.class);
    }
}
