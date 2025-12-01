package com.wonderland.WonderlandApp.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtValidator {
  @Value("${SECRET_KEY}")
  private String SECRET_KEY;

  public Claims isTokenValid(String token) {
      try {
          return Jwts.parserBuilder()
                  .setSigningKey(SECRET_KEY.getBytes())
                  .build()
                  .parseClaimsJws(token)
                  .getBody();
      } catch (JwtException e) {
          return null;
      }
  }
}