package com.wonderland.WonderlandApp.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;

    public JwtAuthFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 1. Si no hay token, pasa la cadena (Usuario es ANONYMOUS)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Intentamos validar el token
        String token = authHeader.substring(7);
        Claims claims = jwtValidator.isTokenValid(token);

        // --- CORRECCIÓN AQUÍ ---
        // Si el token es inválido (claims == null), NO bloqueamos. 
        // Simplemente no autenticamos y dejamos que SecurityConfig decida.
        if (claims != null) {
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            if (role != null) {
                role = role.toUpperCase();
                String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;

                List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority(roleName));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                authorities
                );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // Si claims == null, simplemente seguimos sin poner nada en el contexto.

        filterChain.doFilter(request, response);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Preflight CORS
        if ("OPTIONS".equalsIgnoreCase(method)) return true;

        if ("POST".equals(method) && "/api/v1/auth/login".equals(path)) return true;
        if ("POST".equals(method) && "/api/v1/mensajes".equals(path)) return true;

        if ("GET".equals(method) && path.startsWith("/api/v1/productos")) return true;

        if ("POST".equals(method) && "/api/v1/pedidos".equals(path)) return true;
        if ("GET".equals(method) && path.startsWith("/api/v1/pedidos")) return true;

        return false;
    }
}
