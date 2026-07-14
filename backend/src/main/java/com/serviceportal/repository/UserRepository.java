package com.serviceportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.serviceportal.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
