package com.example.usedmarket.controller;

import com.example.usedmarket.dto.ReplyDto;
import com.example.usedmarket.dto.ResponseDto;
import com.example.usedmarket.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReplyController {
    private final ReplyService service;

    // 댓글 생성
    @PostMapping("/items/{itemId}/reply")
    public ReplyDto createReply(@PathVariable("itemId") Long itemId,
                                @RequestBody ReplyDto dto){
        return service.createReply(itemId, dto);
    }

    // 댓글 조회
    @GetMapping("/items/{itemId}/reply")
    public Page<ReplyDto> readAllReplyPage(
            @RequestParam("page") Integer page,
            @RequestParam("limit") Integer limit
    ){
        return service.readAllReply(page, limit);
    }

    // 댓글 수정
    @PutMapping("/items/{itemId}/reply/{replyId}")
    public ResponseDto updateReply(
            @PathVariable("itemId") Long itemId,
            @PathVariable("replyId") Long id,
            @RequestBody ReplyDto dto) throws IllegalAccessException {
        service.updateReply(itemId, id, dto);
        return ResponseDto.response("댓글 수정이 완료되었습니다.");
    }

    // 댓글 삭제
    @DeleteMapping("/items/{itemId}/reply/{replyId}")
    public ResponseDto deleteReply(
            @PathVariable("itemId") Long itemId,
            @PathVariable("replyId") Long id) throws IllegalAccessException {
        service.deleteReply(itemId, id);
        return ResponseDto.response("댓글이 삭제되었습니다.");
    }
}
