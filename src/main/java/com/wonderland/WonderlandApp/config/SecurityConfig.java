package com.wonderland.WonderlandApp.config;

import org.springframework.http.HttpMethod;
import com.wonderland.WonderlandApp.jwt.*;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;

  public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
          .cors(cors -> cors.configurationSource(request -> {
              var corsConfig = new CorsConfiguration();
              corsConfig.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
              corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
              corsConfig.setAllowedHeaders(List.of("Authorization", "Content-Type"));
              corsConfig.setExposedHeaders(List.of("Authorization"));
              corsConfig.setAllowCredentials(true);
              return corsConfig;
          }))
          .csrf(csrf -> csrf.disable()) 
          .authorizeHttpRequests(auth -> auth

              // 1. RUTAS PÚBLICAS
              .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
              .requestMatchers(HttpMethod.POST, "/api/v1/mensajes").permitAll()
              
              // Productos públicos
              .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()

              // PEDIDOS PÚBLICOS (Crear y Consultar estado)
              .requestMatchers(HttpMethod.POST, "/api/v1/pedidos").permitAll()
              .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/**").permitAll() 

              // 2. RUTAS DE ADMINISTRADOR
              .requestMatchers("/api/v1/productos/**").hasRole("ADMIN") 
              .requestMatchers("/api/v1/empleados/**").hasRole("ADMIN")
              .requestMatchers("/api/v1/mensajes/**").hasRole("ADMIN")

              // 3. RUTAS AUTENTICADAS (Lo que sobre)
              .requestMatchers("/api/v1/detalle-pedidos/**").authenticated()
              
              // ELIMINADA LA LÍNEA: .requestMatchers("/api/v1/pedidos").authenticated() (CAUSABA CONFLICTO)

              .anyRequest().authenticated()
          )
          .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

      return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}