package com.example.usedmarket.dto;

import com.example.usedmarket.entity.SalesItemEntity;
import lombok.Data;

@Data
public class SalesItemDto {
    private Long id;
    private String title;
    private String description;
    private String itemImgUrl;
    private String status;
    private String writer;
    private String minPrice;
    private String password;

    public static SalesItemDto fromEntity(SalesItemEntity entity){
        SalesItemDto dto = new SalesItemDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setItemImgUrl(entity.getItemImgUrl());
        dto.setStatus(entity.getStatus());
        dto.setWriter(entity.getWriter());
        dto.setMinPrice(entity.getMinPrice());
        dto.setPassword(entity.getPassword());
        return dto;
    }
}
