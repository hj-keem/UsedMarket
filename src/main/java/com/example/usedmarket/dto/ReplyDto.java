package com.example.usedmarket.dto;

import com.example.usedmarket.entity.ReplyEntity;
import lombok.Data;

@Data
public class ReplyDto {
    private Long id;
    private Long itemId;
    private String content;
    private String reply;
    private String password;
    private String writer;

    public static ReplyDto fromEntity(ReplyEntity entity){
        ReplyDto dto = new ReplyDto();
        dto.setId(entity.getId());
        dto.setItemId(entity.getItemId());
        dto.setReply(entity.getReply());
        dto.setContent(entity.getContent());
        dto.setWriter(entity.getWriter());
        dto.setPassword(entity.getPassword());
        return dto;
    }
}
