package com.example.usedmarket.controller;

import com.example.usedmarket.dto.NegoDto;
import com.example.usedmarket.dto.ResponseDto;
import com.example.usedmarket.service.NegoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NegoController {
    private final NegoService negoService;

    // 제안 등록
    @PostMapping("/items/{itemId}/proposals")
    public NegoDto createNego(@PathVariable("itemId") Long itemId,
                              @RequestBody NegoDto dto){
        return negoService.createNego(itemId, dto);
    }

    // 제안 읽기
    @GetMapping("/items/{itemId}/proposals")
    public Page<NegoDto> readNegoPage
    (
            @PathVariable("itemId") Long itemId,
            @RequestParam("page") Integer page,
            @RequestParam("limit") Integer limit
    ){
        return negoService.readNegoPage(itemId, page, limit);
    }

    // 제안 수정
    @PutMapping("/items/{itemId}/proposals/{negoId}")
    public NegoDto updateNego(@PathVariable("itemId") Long itemId,
                              @PathVariable("negoId") Long id,
                              @RequestBody NegoDto dto
    ) throws IllegalAccessException {
        return negoService.updateNego(itemId, id, dto);
    }

    // 제안 삭제
    @DeleteMapping("/items/{itemId}/proposals/{negoId}")
    public ResponseDto deleteNego(@PathVariable("itemId") Long itemId,
                                  @PathVariable("negoId") Long id,
                                  @RequestBody NegoDto dto) throws IllegalAccessException
    {
        negoService.deleteNego(itemId, id, dto);
        return ResponseDto.response("제안이 삭제되었습니다.");
    }

    // 제안 수락 | 거절
    @PutMapping("/items/{itemId}/proposals/{negoId}/status")
    public NegoDto status(
            @PathVariable("itemId") Long itemId,
            @PathVariable("negoId") Long id,
            @RequestBody NegoDto dto
    ) throws IllegalAccessException {
        return negoService.acceptOrReject(itemId, id, dto);
    }

    @PutMapping("/items/{itemId}/proposals/{negoId}/confirm")
    public NegoDto confirm(
            @PathVariable("itemId") Long itemId,
            @PathVariable("negoId") Long id,
            @RequestBody NegoDto dto
    ) throws IllegalAccessException {
        return negoService.purchaseConfirm(itemId, id, dto);
    }
}
