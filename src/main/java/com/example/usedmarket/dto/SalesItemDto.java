package com.example.usedmarket.dto;

import com.example.usedmarket.entity.SalesItemEntity;
import com.example.usedmarket.entity.UserEntity;
import lombok.Data;

@Data
public class SalesItemDto {
    private Long id;
    private String title;
    private String description;
    private String itemImgUrl;
    private String status;
    private String minPrice;
//    private UserEntity addUser;

    public static SalesItemDto fromEntity(SalesItemEntity entity){
        SalesItemDto dto = new SalesItemDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setItemImgUrl(entity.getItemImgUrl());
        dto.setStatus(entity.getStatus());
        dto.setMinPrice(entity.getMinPrice());
//        dto.setAddUser(entity.getAddUser());

        return dto;
    }
}
