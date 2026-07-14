package com.serviceportal.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller für den Endpunkt /api/me
 *
 * Zweck (Deutsch):
 * - Liefert Informationen über den aktuell authentifizierten Benutzer.
 * - Wenn keine Authentifizierung vorhanden ist, wird eine leere Map zurückgegeben.
 * - Rückgabe: JSON-Objekt mit 'username' und 'roles'.
 */
@RestController
public class MeController {

    @GetMapping("/api/me")
    public Map<String, Object> me(Authentication authentication) {
        if (authentication == null) {
            return Map.of();
        }
        String username = authentication.getName();
        var roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        return Map.of("username", username, "roles", roles);
    }
}
