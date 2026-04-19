package com.liveStock.tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 🔥 Disable CSRF for WebSocket (VERY IMPORTANT)
                .csrf(csrf -> csrf.disable())

                // 🔓 Allow WebSocket + UI access
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",              // home page
                                "/ws/**",         // websocket endpoint
                                "/topic/**",      // broker topics
                                "/app/**",        // application endpoints (if used)
                                "/js/**",
                                "/css/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                )

                // ❌ Disable default login form (optional but clean)
                .formLogin(form -> form.disable())

                // ❌ Disable HTTP Basic (optional)
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
}