package com.example.bankcards.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // REST API: обычно csrf выключают (особенно если JWT будет)
                .csrf(csrf -> csrf.disable())

                // Без сессий
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Пока используем basic (временно, до JWT) чтобы можно было проверять auth
                .httpBasic(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        // health (если actuator подключён)
                        .requestMatchers("/actuator/health").permitAll()

                        // swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // всё остальное закрыто
                        .anyRequest().authenticated()
                )
                .build();
    }
}