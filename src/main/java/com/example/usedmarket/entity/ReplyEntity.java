package com.example.usedmarket.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ReplyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long itemId;
    private String content;
    private String reply;
    private String password;
    private String writer;

    @ManyToOne
    private UserEntity addUser;
    @ManyToOne
    private SalesItemEntity addItem;
}
