package com.example.thesis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    public static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private DataSource dataSource;
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    // 配置密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return encoder;
    }

    // 配置 AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    // 配置 UserDetailsService 使用 JDBC 进行用户认证
    @Bean
    public UserDetailsService userDetailsService() {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);

        // 配置查询用户信息的 SQL
        userDetailsManager.setUsersByUsernameQuery("SELECT username, password, enabled FROM user WHERE username = ?");
        userDetailsManager.setAuthoritiesByUsernameQuery("SELECT username, role FROM user WHERE username = ?");

        return userDetailsManager;
    }

    // 配置 HTTP 安全规则
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for simplicity
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register").permitAll()
                .requestMatchers("/auth/welcome").permitAll() // Permit all access to /auth/welcome
                .requestMatchers("/auth/user/**").authenticated() // Require authentication for /auth/user/**
                .requestMatchers("/auth/admin/**").authenticated() // Require authentication for /auth/admin/**
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/teacher/**").hasRole("TEACHER")
                .requestMatchers("/api/student/**").hasRole("STUDENT")
            )
            .formLogin(form -> form
                .loginPage("/login")
                //.loginProcessingUrl("/announcementList")
                .successHandler(authenticationSuccessHandler)
                .failureUrl("/login?error=true")
                .permitAll()
            );

        return http.build();
    }
}

