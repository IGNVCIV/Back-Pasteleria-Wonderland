package com.wonderland.WonderlandApp.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Component
public class JwtGenerator {

    @Value("${SECRET_KEY}")
    private String SECRET_KEY;

    public String generateToken(com.wonderland.WonderlandApp.model.User user) {

        long expirationMillis =  Duration.ofHours(1).toMillis(); // 1 hora

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .claim("userId", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes())
                .compact();
    }
}
