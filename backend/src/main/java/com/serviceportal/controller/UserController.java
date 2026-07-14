package com.serviceportal.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.serviceportal.model.User;
import com.serviceportal.repository.UserRepository;

/**
 * REST-Controller für Benutzerverwaltung (/api/users)
 *
 * Beschreibung (Deutsch):
 * - GET /api/users: Liste aller Benutzer (nur ROLE_ADMIN)
 * - PUT /api/users/{id}/roles: Rollen eines Benutzers ändern (nur ROLE_ADMIN)
 *
 * Hinweis: Dieses API ist minimal gestaltet und dient dem Demo-/Admin-Zweck.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository repo;
    public UserController(UserRepository repo) { this.repo = repo; }

    /**
     * Alle Benutzer zurückgeben (Admin)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> list() { return repo.findAll(); }

    /**
     * Rollen eines Benutzers aktualisieren (Admin)
     */
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateRoles(@PathVariable Long id, @RequestBody User u) {
        return repo.findById(id).map(existing -> { existing.setRoles(u.getRoles()); return repo.save(existing); }).orElse(null);
    }
}
