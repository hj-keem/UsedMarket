package com.example.usedmarket.controller;

import com.example.usedmarket.dto.ResponseDto;
import com.example.usedmarket.dto.SalesItemDto;
import com.example.usedmarket.service.SalesItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SalesItemController {
    private final SalesItemService service;

    // create
    // 물품 정보 등록
    @PostMapping("/items")
    public SalesItemDto createItem(@RequestBody SalesItemDto dto){
        return service.createItem(dto);
    }

    // readItem
    // @PathVariable을 이용하여 url에서의 변수값을 추출
    @GetMapping("/items/{itemId}")
    public SalesItemDto readItem(@PathVariable("itemId") Long id){
        return service.readItem(id);
    }

    // readAll
    @GetMapping("/items")
    public Page<SalesItemDto> readAllPaged(
            @RequestParam("page") Integer page,
            @RequestParam("limit") Integer limit) {
        return service.readAllPaged(page, limit);
    }

    // updateItem
    // 물품 정보 수정
    @PutMapping("/items/{itemId}")
    public ResponseDto updateItem(@PathVariable("itemId") Long id,
                                  @RequestBody SalesItemDto dto){
        service.updateItem(id, dto);
        return ResponseDto.response("물품 정보가 수정되었습니다.");
    }


    // updateItem
    // 물품 정보에 이미지 첨부
    @PutMapping(value = "/items/{itemId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto updateImage(
            @PathVariable("itemId")Long id,
            @RequestParam("image") MultipartFile itemImage)throws IOException {
        return service.updateImage(id,itemImage);
    }

    // deleteItem
    // 등록된 물품 삭제
    @DeleteMapping("/items/{itemId}")
    public ResponseDto deleteItem(@PathVariable("itemId")Long id){
        service.deleteItem(id);
        return ResponseDto.response("물품이 삭제되었습니다.");
    }
}
