package com.example.usedmarket.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class NegoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long itemId;
    private String suggestedPrice;
    private String status;
    private String writer;
    private String password;

    // 다대일 관계를 설정 (NegoEntity에서 관계설정을 했기 때문에 SalesItemEntity에는 따로 해주지 않아도 됨)
    @ManyToOne
    @JoinColumn(name = "salesItemId")
    private SalesItemEntity salesItem;
}
