package com.example.usedmarket.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String password;
    private String passwordCheck;
}