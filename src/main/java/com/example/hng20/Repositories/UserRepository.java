package com.example.hng20.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.example.hng20.Models.User;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
