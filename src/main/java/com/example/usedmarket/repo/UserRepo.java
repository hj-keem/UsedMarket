package com.example.usedmarket.repo;

import com.example.usedmarket.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    UserEntity findAllByUsername(String username);
    boolean existsByUsername(String username);
}