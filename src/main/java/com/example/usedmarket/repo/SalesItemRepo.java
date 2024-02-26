package com.example.usedmarket.repo;

import com.example.usedmarket.entity.SalesItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesItemRepo extends JpaRepository<SalesItemEntity, Long> {
    List<SalesItemEntity> findAllBy();
}
