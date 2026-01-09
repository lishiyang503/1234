package org.example.springboot_vaue_demo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    private static final String SECRET_KEY = "7a4d9f8b3e2c1a6f5e4d3c2b1a7f8e9d6c5b4a3d2e1f0a9b8c7d6e5f4a3b2c1d";
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;
    
    public static String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", role);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public static String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("username", String.class);
    }
    
    public static String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }
    
    public static boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    private static Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    private static SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}