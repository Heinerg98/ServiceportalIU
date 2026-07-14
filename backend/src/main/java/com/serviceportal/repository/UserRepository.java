package com.serviceportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serviceportal.model.User;

/**
 * Repository-Interface für User-Entitäten.
 *
 * Ergänzende Methode: findByUsername zur Suche nach Benutzern anhand des Nutzernamens.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
