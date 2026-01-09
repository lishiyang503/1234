package com.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF 保护，因为我们使用 JWT
            .csrf(AbstractHttpConfigurer::disable)
            // 禁用会话管理，因为我们使用 JWT
            .sessionManagement(AbstractHttpConfigurer::disable)
            // 配置请求授权
            .authorizeHttpRequests(authorizeHttpRequests ->
                authorizeHttpRequests
                    // 允许所有请求
                    .anyRequest().permitAll()
            )
            // 配置 CORS
            .cors(cors -> cors.configurationSource(request -> {
                org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
                configuration.setAllowedOriginPatterns(java.util.List.of("*"));
                configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(java.util.List.of("*"));
                configuration.setExposedHeaders(java.util.List.of("Authorization"));
                configuration.setMaxAge(3600L);
                return configuration;
            }));

        return http.build();
    }
}
