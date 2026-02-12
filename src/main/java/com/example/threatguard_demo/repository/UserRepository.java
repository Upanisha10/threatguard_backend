package com.example.threatguard_demo.repository;

import com.example.threatguard_demo.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);

    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);
}
