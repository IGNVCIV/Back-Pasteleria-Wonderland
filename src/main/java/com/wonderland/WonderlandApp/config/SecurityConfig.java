package com.wonderland.WonderlandApp.config;

import org.springframework.http.HttpMethod;
import com.wonderland.WonderlandApp.jwt.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;

  public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
          .csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests(auth -> auth

              .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
              .requestMatchers(HttpMethod.POST, "/api/v1/mensajes").permitAll()
              .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()
              .requestMatchers(HttpMethod.POST, "/api/v1/pedidos").permitAll()
              .requestMatchers(HttpMethod.GET, "/api/v1/pedidos/*").permitAll()

              .requestMatchers(HttpMethod.POST, "/api/v1/productos/**").hasRole("ADMIN")
              .requestMatchers(HttpMethod.PUT, "/api/v1/productos/**").hasRole("ADMIN")
              .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/**").hasRole("ADMIN")
              .requestMatchers("/api/v1/empleados/**").hasRole("ADMIN")

              .requestMatchers(HttpMethod.GET, "/api/v1/mensajes/**").hasAnyRole("ADMIN", "EMPLEADO")
              .requestMatchers(HttpMethod.PUT, "/api/v1/mensajes/**").hasAnyRole("ADMIN", "EMPLEADO")
              .requestMatchers(HttpMethod.DELETE, "/api/v1/mensajes/**").hasAnyRole("ADMIN", "EMPLEADO")


              .requestMatchers("/api/v1/pedidos/**").authenticated()
              .requestMatchers("/api/v1/detalle-pedidos/**").authenticated()

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