package com.example.usedmarket.repo;

import com.example.usedmarket.entity.NegoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NegoRepo extends JpaRepository<NegoEntity, Long> {
}
