// src/main/java/com/example/job_platform/util/JwtUtil.java
package com.example.job_platform.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JSON Web Token (JWT) operations.
 * Handles generation of JWT tokens.
 * This version uses a simple hardcoded key for demonstration.
 * In a production environment, this key should be loaded securely
 * (e.g., from environment variables, Vault, etc.) and rotated regularly.
 */
@Component
public class JwtUtil {

    // IMPORTANT: In a real application, never hardcode the secret key!
    // It should be a strong, randomly generated key stored securely (e.g., environment variable).
    // Using Keys.secretKeyFor(SignatureAlgorithm.HS256) for a simple, quick key generation.
    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generates a secure random key

    // Token expiration time (e.g., 10 hours in milliseconds)
    public static final long JWT_TOKEN_VALIDITY = 10 * 60 * 60 * 1000; // 10 hours

    /**
     * Generates a JWT token for a given subject (username).
     *
     * @param username The username for whom the token is generated.
     * @return The generated JWT string.
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>(); // You can add more claims like roles here
        return createToken(claims, username);
    }

    /**
     * Creates the JWT token with specified claims and subject, setting issuance and expiration dates.
     *
     * @param claims Additional claims to be included in the token.
     * @param subject The subject (e.g., username) of the token.
     * @return The JWT string.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // Set the claims (e.g., custom data)
                .setSubject(subject) // Set the subject (e.g., username)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set the token's issuance time
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)) // Set expiration time
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // Sign the token with the secret key and algorithm
                .compact(); // Build and compact the JWT into a string
    }

    /**
     * Extracts all claims from a JWT token.
     * Useful for debugging or if you wanted to read claims (not strictly needed for this request).
     *
     * @param token The JWT token string.
     * @return The Claims object containing all claims.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    /**
     * Extracts a single claim from the token using a claims resolver function.
     *
     * @param token The JWT token string.
     * @param claimsResolver A function to resolve a specific claim from the Claims object.
     * @param <T> The type of the claim to be extracted.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts the username from the JWT token.
     *
     * @param token The JWT token string.
     * @return The username (subject) from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Checks if the token has expired.
     *
     * @param token The JWT token string.
     * @return true if the token is expired, false otherwise.
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the token.
     *
     * @param token The JWT token string.
     * @return The expiration Date of the token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
