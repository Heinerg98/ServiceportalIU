package com.serviceportal.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.serviceportal.model.User;
import com.serviceportal.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository repo;
    public UserController(UserRepository repo) { this.repo = repo; }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> list() { return repo.findAll(); }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateRoles(@PathVariable Long id, @RequestBody User u) {
        return repo.findById(id).map(existing -> { existing.setRoles(u.getRoles()); return repo.save(existing); }).orElse(null);
    }
}
