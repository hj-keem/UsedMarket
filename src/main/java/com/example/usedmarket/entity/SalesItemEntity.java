package com.example.usedmarket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class SalesItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String itemImgUrl;
    private String status;
    private String minPrice;

    @ManyToOne
    private UserEntity addUser;
    @OneToMany
    private List<ReplyEntity> replyList = new ArrayList<>();
    @OneToMany
    private List<NegoEntity>negoList = new ArrayList<>();
}
