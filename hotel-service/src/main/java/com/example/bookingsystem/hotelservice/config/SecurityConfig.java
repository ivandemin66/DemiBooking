package com.example.bookingsystem.hotelservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация безопасности для Hotel Service
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Настройка цепочки фильтров безопасности
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Разрешаем все запросы для Hotel Service
                // В реальной системе здесь должна быть проверка JWT токенов
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
