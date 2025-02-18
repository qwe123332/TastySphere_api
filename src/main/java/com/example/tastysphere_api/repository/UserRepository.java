package com.example.tastysphere_api.repository;

import com.example.tastysphere_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    User findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    long countByActive(boolean active);
    long countByCreatedTimeAfter(Timestamp time);


}