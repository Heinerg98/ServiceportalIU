package com.serviceportal.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.http.HttpStatus;

import com.serviceportal.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo; this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> this.userRepo.findByUsername(username)
            .map(u -> {
                String[] roles = u.getRoles() != null ? u.getRoles().split(",") : new String[]{"ROLE_USER"};
                UserDetails ud = User.withUsername(u.getUsername()).password(u.getPassword()).roles(Arrays.stream(roles).map(r -> r.replace("ROLE_",""
                )).toArray(String[]::new)).build();
                return ud;
            }).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/offers/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/requests/**")).permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .httpBasic();

        // for H2 console
        http.headers().frameOptions().disable();

        return http.build();
    }
}
