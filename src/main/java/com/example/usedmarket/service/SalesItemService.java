package com.example.usedmarket.service;

import com.example.usedmarket.dto.ResponseDto;
import com.example.usedmarket.dto.SalesItemDto;
import com.example.usedmarket.entity.SalesItemEntity;
import com.example.usedmarket.repo.SalesItemRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor //final의 생성자를 만들어준다.
public class SalesItemService {
    private final SalesItemRepo repository;

    // createItem
    public SalesItemDto createItem(SalesItemDto dto) {
        SalesItemEntity itemEntity = new SalesItemEntity();
        itemEntity.setTitle(dto.getTitle());
        itemEntity.setDescription(dto.getDescription());
        itemEntity.setItemImgUrl(dto.getItemImgUrl());
        itemEntity.setStatus(dto.getStatus());
        itemEntity.setWriter(dto.getWriter());
        itemEntity.setPassword(dto.getPassword());

        return SalesItemDto.fromEntity(repository.save(itemEntity));
    }

    // readItem
    public SalesItemDto readItem(Long id){
        Optional<SalesItemEntity> entity = repository.findById(id);
        if(entity.isEmpty())
            // 만약 찾고자하는 id의 데이터가 없다면 404에러
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return SalesItemDto.fromEntity(entity.get());
    }

    // readAllItem
    public SalesItemDto readAllItem(){
        List<SalesItemEntity> itemList = repository.findAll();
        // for(타입 변수명 : 콜렉션명)
        for(SalesItemEntity item : itemList){
            itemList.add(item);
        }
        return (SalesItemDto) itemList;
    }

    public Page<SalesItemDto> readAllPaged(Integer page, Integer limit) {
        // 객체 생성 및 첫 번째 페이지(0)에 해당하는 아이템을 3개씩 조회
        Pageable pageable = PageRequest.of(0, 25);
        // findAll 호출 시 Pageable 전달
        // Page: 페이지에 대한 정보를 포함한 객체
        Page<SalesItemEntity> itemEntityPage = repository.findAll(pageable);
        // 순회
        List<SalesItemDto> itemDtoList = new ArrayList<>();
        // for(타입 변수명 : 콜렉션명
        for (SalesItemEntity entity : itemEntityPage) {
            itemDtoList.add(SalesItemDto.fromEntity(entity));
        }
        return itemEntityPage.map(SalesItemDto::fromEntity);
    }

    /* update
    1. checkUser를 이용하여 사용자 검증
        1) param : body에서 요청으로 들어온 값이므로, 현재 db에 있는 값과 비교 가능
    2. 데이터에 반영할 값은 따로 저장
     */
    public SalesItemDto updateItem(Long id, SalesItemDto dto) throws IllegalAccessException {
        // 유저 검증
        SalesItemEntity salesEntity = checkUser(id,dto.getWriter(),dto.getPassword());
        salesEntity.setMinPrice(dto.getMinPrice());
        return SalesItemDto.fromEntity(repository.save(salesEntity));
    }

    // image
    public ResponseDto updateImage(Long id, MultipartFile itemImage) {
        // salesEntity에서 게시물 찾기
        Optional<SalesItemEntity> optionalItem = repository.findById(id);
        // 게시물 존재 확인
        if(optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // 2. 파일을 어디에 업로드 할건지
        // media/{userId}/profile.{파일 확장자}
        // 2-1. 폴더만 만드는 과정
        String profileDir = "media/items/";
        try {
            Files.createDirectories(Path.of(profileDir));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 2-2. 확장자를 포함한 이미지 이름 만들기 (profile.{확장자})
        String originalFilename = itemImage.getOriginalFilename();
        String[] fileNameSplit = originalFilename.split("\\.");
        String extension = fileNameSplit[fileNameSplit.length - 1]; //마지막 배열은 확장자가 되겠지?
        String profileFilename = "Image." + extension;

        // 2-3. 폴더와 파일 경로를 포함한 이름 만들기
        String profilePath = profileDir + profileFilename;

        // 3. MultipartFile 을 저장하기
        try {
            itemImage.transferTo(Path.of(profilePath));
        } catch (IOException e) {
            log.info(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 4. Entity 업데이트 (정적 프로필 이미지를 회수할 수 있는 URL)
            // http://localhost:8080/static/1/profile.png
            // SalesItemEntity salesItemEntity = optionalItem.get();
            // 찾은 Id를 salesItemEntity 변수에 저장
        SalesItemEntity salesItemEntity = optionalItem.get();
        salesItemEntity.setItemImgUrl(String.format("/static/%d/%s", id, profileFilename));
        repository.save(salesItemEntity);
        return ResponseDto.response("이미지가 등록되었습니다.");
    }

    /* delete
    게시글 id과 일치하는 게시글 삭제
    */
    public void deleteItem(Long id){
        Optional<SalesItemEntity> optionalItem = repository.findById(id);
        if(optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        SalesItemEntity salesEntity = optionalItem.get();
        repository.deleteById(id);
    }

    /* 사용자 일치를 판단하는 checkUser()
    - id, writer, password 입력 시 기존의 db의 회원 정보와 일치하는지 확인
    - id : 게시글 id
    - writer : 게시글 작성자
    - password : 게시글 작성자가 첨부한 비밀번호
     */
    public SalesItemEntity checkUser(Long id, String writer, String password) throws IllegalAccessException {
        Optional<SalesItemEntity> optionalItem = repository.findById(id);
        if(optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        SalesItemEntity entity = optionalItem.get();

        if(!entity.getWriter().equals(writer) || !entity.getPassword().equals(password))
            throw new IllegalAccessException("사용자 정보가 일치하지 않습니다.");
        return entity;
    }

}
