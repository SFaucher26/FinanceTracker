package com.simplon.FinanceTracker.Configuration;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtils {

    @Value("${spring.jwt.secret}")
    private String jwtSecret;

    @Value("${spring.jwt.expiration}")
    private long jwtExpirationInMs;

    // Generate a JWT token using the username as subject.
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        byte[] keyBytes = jwtSecret.getBytes();
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(keyBytes))
                .compact();
    }

    // Extract username from JWT token.
    public String getUsernameFromJWT(String token) {
        SecretKey secret = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Claims claims = Jwts.parser()
                .verifyWith(secret)
                .decryptWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // Validate the JWT token.
    public boolean validateToken(String authToken) {
        try {
            SecretKey secret = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
}
