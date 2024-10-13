package com.brokerage.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;

public class JwtHelper {

    private static final int MINUTES = 60;
    private static final String SECRET_KEY = "88cd2108b5347d973cf39cdf9053d7dd42704876d8c9a9bd8e2d168259d3ddf7";
    public static String generateToken(String id, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", id);
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 + 24))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();

    }
    private static SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    public static String extractUsername(String token) {
        return getTokenBody(token).getSubject();
    }

    public static Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private static Claims getTokenBody(String token) {
        try {
            return Jwts
                    .parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SignatureException | ExpiredJwtException e) { // Invalid signature or expired token
            throw new AccessDeniedException("Access denied: " + e.getMessage());
        }
    }

    private static boolean isTokenExpired(String token) {
        Claims claims = getTokenBody(token);
        return claims.getExpiration().before(new Date());
    }

    public static UUID extractUserId(String token) {
        Claims claims = extractAllClaims(token);

        String userIdStr = claims.get("userId", String.class);

        return UUID.fromString(userIdStr);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).build().parseSignedClaims(token).getPayload();
    }
}
