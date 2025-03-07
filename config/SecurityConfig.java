package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // 禁用 CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/register", "/register.html","/api/login","/login.html","api/forgot-password","/forgot-password.html",
                                "api/reset-password" ,"/reset-password.html","/api/user-email","user/index.html","user/**","/profile","/profile.html","/upload","user/upload",
                                     "/","/ask","/api/send-email","/send.html","static/**","/index.html","/css/**","/fonts/**","/images/**","/js/**","/sass/**","/sy/**","/*.html","/chat","/vide/**","/convert","/chat-v2","/ask-v2" ).permitAll()
                        .anyRequest().authenticated() // 其他请求需认证
                );
        return http.build();
    }
}