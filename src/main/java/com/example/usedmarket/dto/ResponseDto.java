package com.example.usedmarket.dto;

import lombok.Data;

@Data
public class ResponseDto {
    private String message;

    public static ResponseDto response(String message) {
        ResponseDto dto = new ResponseDto();
        dto.setMessage(message);
        return dto;
    }
}
