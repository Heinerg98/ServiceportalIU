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

/**
 * Security-Konfiguration für die Anwendung.
 *
 * Hinweise auf Deutsch:
 * - Autorisierungskonfiguration: öffentliche Endpunkte und geschützte Endpunkte
 * - ExceptionHandling: bei nicht-authentifizierten Zugriffen wird ein 401 zurückgegeben
 *   (ohne WWW-Authenticate Header), damit der Browser nicht das native Login-Popup zeigt.
 * - httpBasic() wird hier noch genutzt (für einfache lokale Tests). Für Produktion sollte
 *   eine sicherere Session-/Token-Lösung verwendet werden.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo; this.passwordEncoder = passwordEncoder;
    }

    /**
     * UserDetailsService, das Benutzer aus der Datenbank lädt (app_user table)
     * Die Rollen werden als Komma-getrennte Liste in der roles-Spalte erwartet.
     */
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

    /**
     * SecurityFilterChain: legt die Zugriffsregeln fest
     * - /h2-console ist erlaubt (nur für Entwicklung)
     * - /api/offers und /api/requests sind öffentlich
     * - alle anderen Pfade erfordern Authentifizierung
     * - bei fehlender Authentifizierung wird ein 401 ohne Browser-Challenge zurückgegeben
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // für DEV; in Produktion muss CSRF geprüft werden
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/offers/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/requests/**")).permitAll()
                .anyRequest().authenticated()
            )
            // Bei nicht-autorisierter Anfrage: 401 (keine WWW-Authenticate-Challenge)
            .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .httpBasic(); // Einfaches Basis-Auth für lokale Entwicklung

        // Erlaube H2-Console Frames (nur für Entwicklung)
        http.headers().frameOptions().disable();

        return http.build();
    }
}
