/*package com.example.demo.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    @Order(0)
    SecurityFilterChain permitAll(HttpSecurity http) throws Exception {
        return http
                .securityMatcher("/**")                 // 모든 요청 매칭
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin(f -> f.disable())            // 로그인 폼 비활성
                .httpBasic(b -> b.disable())            // Basic 비활성
                .build();
    }

}*/
