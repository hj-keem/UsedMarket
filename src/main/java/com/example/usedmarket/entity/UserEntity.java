package com.example.usedmarket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // DB 제약사항 추가
    @Column(nullable = false, unique = true)
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;

    private String email;
    private String phone;

    @OneToMany
    private List<SalesItemEntity> itemList = new ArrayList<>();
    @OneToMany
    private List<ReplyEntity> commentList = new ArrayList<>();
    @OneToMany
    private List<NegoEntity> negoList = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles")
    private List<Role> roles = new ArrayList<>();


}