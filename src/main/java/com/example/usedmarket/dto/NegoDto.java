package com.example.usedmarket.dto;

import com.example.usedmarket.entity.NegoEntity;
import lombok.Data;

@Data
public class NegoDto {
    private Long id;
    private Long itemId;
    private String suggestedPrice;
    private String status;
//    private String writer;
//    private String password;

    public static NegoDto fromEntity(NegoEntity entity){
        NegoDto dto = new NegoDto();
        dto.setId(entity.getId());
        dto.setItemId(entity.getItemId());
        dto.setSuggestedPrice(entity.getSuggestedPrice());
        dto.setStatus(entity.getStatus());
//        dto.setWriter(entity.getWriter());
//        dto.setPassword(entity.getPassword());
        return dto;
    }
}
