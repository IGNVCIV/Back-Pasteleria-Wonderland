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

  private Key getCompatibleKey() {
    byte[] keyBytes = SECRET_KEY.getBytes();
    byte[] padded = new byte[32];
    System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32)); // Copiamos solo lo que quepa, el resto queda en 0
    return new SecretKeySpec(padded, "HmacSHA256");
  }

  public Claims isTokenValid(String token) {
      try {
          Key key = getCompatibleKey();// <--- clave como bytes, no Key

          return Jwts.parserBuilder()
                  .setSigningKey(key)
                  .build()
                  .parseClaimsJws(token)
                  .getBody();
      } catch (JwtException e) {
          return null;
      }
  }
}