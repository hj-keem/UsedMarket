package com.example.usedmarket.repo;

import com.example.usedmarket.entity.NegoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NegoRepo extends JpaRepository<NegoEntity, Long> {
    List<NegoEntity> findByItemId(Long itemId);
}
