package com.example.usedmarket.service;

import com.example.usedmarket.dto.ResponseDto;
import com.example.usedmarket.dto.SalesItemDto;
import com.example.usedmarket.entity.SalesItemEntity;
import com.example.usedmarket.entity.UserEntity;
import com.example.usedmarket.repo.SalesItemRepo;
import com.example.usedmarket.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    private final SalesItemRepo itemRepository;
    private final UserRepo userRepository;

    /* createItem
    로그인 한 객체를 받아오는 방법
    1. SecurityContextHolder를 이용해 사용자 정보를 받아온다.
    2. 받아온 정보를 salesItem Entity에 올린다. ( 사용자 정보 기억 )
    */
    public SalesItemDto createItem(SalesItemDto dto) {
        // 현재 사용자의 인증 정보를 가져오기
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findAllByUsername(auth);

        SalesItemEntity itemEntity = new SalesItemEntity();
        itemEntity.setTitle(dto.getTitle());
        itemEntity.setDescription(dto.getDescription());
        itemEntity.setItemImgUrl(dto.getItemImgUrl());
        itemEntity.setStatus(dto.getStatus());
        itemEntity.setAddUser(user);

        return SalesItemDto.fromEntity(itemRepository.save(itemEntity));
    }

    /* readItem
    모든 사람이 읽을 수 있으므로 권한설정을 GET은 전부 permitAll()
    */
    public SalesItemDto readItem(Long id){
        Optional<SalesItemEntity> entity = itemRepository.findById(id);
        if(entity.isEmpty())
            // 만약 찾고자하는 id의 데이터가 없다면 404에러
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return SalesItemDto.fromEntity(entity.get());
    }


    // readAllItem
    public Page<SalesItemDto> readAllPaged(Integer page, Integer limit) {
        // 객체 생성 및 첫 번째 페이지(0)에 해당하는 아이템을 3개씩 조회
        Pageable pageable = PageRequest.of(0, 25);
        // findAll 호출 시 Pageable 전달
        // Page: 페이지에 대한 정보를 포함한 객체
        Page<SalesItemEntity> itemEntityPage = itemRepository.findAll(pageable);
        // 순회
        List<SalesItemDto> itemDtoList = new ArrayList<>();
        // for(타입 변수명 : 콜렉션명
        for (SalesItemEntity entity : itemEntityPage) {
            itemDtoList.add(SalesItemDto.fromEntity(entity));
        }
        return itemEntityPage.map(SalesItemDto::fromEntity);
    }

    /* update
    게시글 수정하기
    1. SecurityContextHolder를 이용해 사용자 정보를 받아온다.
    2. salesEntity에 있는 사용자 정보와 일치하는 지 확인한다.
    3. 일치한다면 로직 진행
    */
    public SalesItemDto updateItem(Long id, SalesItemDto dto) throws IllegalAccessException {
        // 현재 사용중인 사용자의 정보를 받아온다.
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findAllByUsername(auth);

        // 게시글이 존재하는지 확인
        Optional<SalesItemEntity> optionalItem = itemRepository.findById(id);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        SalesItemEntity salesEntity = optionalItem.get();

        // 현재 사용중인 유저가 게시글을 올린 DB의 유저와 일치한다면 진행
        if (!user.equals(salesEntity.getAddUser()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        salesEntity.setTitle(dto.getTitle());
        salesEntity.setDescription(dto.getDescription());
        salesEntity.setMinPrice(dto.getMinPrice());
        salesEntity.setStatus(dto.getStatus());
        return SalesItemDto.fromEntity(itemRepository.save(salesEntity));
    }

    // image
    public ResponseDto updateImage(Long id, MultipartFile itemImage, String writer, String password) throws IllegalAccessException, IOException {
        // 현재 사용중인 사용자의 정보를 받아온다.
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findAllByUsername(auth);

        // 게시글이 존재하는지 확인
        Optional<SalesItemEntity> optionalItem = itemRepository.findById(id);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        SalesItemEntity salesEntity = optionalItem.get();

        // 이미지 폴더 생성 및 이미지 url 생성
        String profileDir = "media/items/";
        try {
            Files.createDirectories(Path.of(profileDir));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 확장자를 포함한 이미지 이름 만들기 (profile.{확장자})
        String[] fileNameSplit = itemImage.getOriginalFilename().split("\\.");
        String extension = fileNameSplit[fileNameSplit.length - 1]; // 확장자 추출
        String fileName = System.currentTimeMillis() + "." + extension;
        itemImage.transferTo(Path.of(profileDir + fileName));

        // 이미지 URL 생성
        String crerateImageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("static/img/") // 이미지 업로드 경로
                .path(fileName)
                .toUriString();

        salesEntity.setItemImgUrl(crerateImageUrl);
        itemRepository.save(salesEntity);
        return ResponseDto.response("이미지가 등록되었습니다.");
    }

    /* delete
    1. SecurityContextHolder를 이용해 사용자 정보를 받아온다.
    2. salesEntity에 있는 사용자 정보와 일치하는 지 확인한다.
    3. 일치한다면 로직 진행
    */
    public void deleteItem(Long id) throws IllegalAccessException {
        // 현재 사용중인 사용자 정보 받아오기
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findAllByUsername(auth);

        // 게시물 찾기
        Optional<SalesItemEntity> optionalItem = itemRepository.findById(id);
        // 게시물이 존재하는 지 확인
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        SalesItemEntity salesEntity = optionalItem.get();

        // 현재 사용중인 유저가 게시글을 올린 DB의 유저와 일치한다면 진행
        if (!user.equals(salesEntity.getAddUser()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        itemRepository.deleteById(id);
    }


    /* 사용자 일치를 판단하는 checkUser()
    - id, writer, password 입력 시 기존의 db의 회원 정보와 일치하는지 확인
    - id : 게시글 id
    - writer : 게시글 작성자
    - password : 게시글 작성자가 첨부한 비밀번호
     */
//    public SalesItemEntity checkUser(Long id, String writer, String password) throws IllegalAccessException {
//        Optional<SalesItemEntity> optionalItem = itemRepository.findById(id);
//        if(optionalItem.isEmpty())
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        SalesItemEntity entity = optionalItem.get();
//
//        if(!entity.getWriter().equals(writer) || !entity.getPassword().equals(password))
//            throw new IllegalAccessException("사용자 정보가 일치하지 않습니다.");
//        return entity;
//    }
}
