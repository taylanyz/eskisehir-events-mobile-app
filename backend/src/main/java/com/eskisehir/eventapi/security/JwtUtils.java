package com.eskisehir.eventapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT token generation, validation and parsing utility class.
 * Stateless authentication using JSON Web Tokens.
 */
@Component
public class JwtUtils {

    @Value("${app.jwt.secret:mySecretKeyForJwtTokenGenerationAndValidationPurposesDuringDevelopmentPhase}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpirationMs; // Default: 24 hours

    /**
     * Generate JWT token for authenticated user.
     *
     * @param userId User ID to encode in token
     * @param email User email to encode in token
     * @return JWT token string
     */
    public String generateJwtToken(Long userId, String email) {
        return buildToken(userId, email, jwtExpirationMs);
    }

    /**
     * Generate refresh token with extended expiration.
     *
     * @param userId User ID to encode in token
     * @param email User email to encode in token
     * @return JWT refresh token string
     */
    public String generateRefreshToken(Long userId, String email) {
        return buildToken(userId, email, jwtExpirationMs * 7); // 7x longer (7 days)
    }

    private String buildToken(Long userId, String email, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Extract user ID from JWT token.
     *
     * @param token JWT token
     * @return User ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        Object userIdObj = claims.get("userId");
        if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        }
        return (Long) userIdObj;
    }

    /**
     * Extract email from JWT token.
     *
     * @param token JWT token
     * @return User email
     */
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Validate JWT token.
     *
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Parse and get claims from JWT token.
     *
     * @param token JWT token
     * @return Claims object
     */
    private Claims getClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
