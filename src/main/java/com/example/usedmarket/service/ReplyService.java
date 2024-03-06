package com.example.usedmarket.service;

import com.example.usedmarket.dto.ReplyDto;
import com.example.usedmarket.entity.ReplyEntity;
import com.example.usedmarket.entity.SalesItemEntity;
import com.example.usedmarket.repo.ReplyRepo;
import com.example.usedmarket.repo.SalesItemRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor //final의 생성자를 만들어준다.
public class ReplyService {
    private final ReplyRepo replyRepository;
    private final SalesItemRepo itemRepository;

    /* 댓글 생성
    1. 물품 게시글의 id를 먼저 존재하는 지 확인
    2. 존재한다면 해당 id에 댓글 생성
    3. 대상 물품, 댓글 내용, 작성자가 포함되어야 한다.
    */
    public ReplyDto createReply(Long itemId, ReplyDto dto){
        Optional<SalesItemEntity> itemOptional = itemRepository.findById(itemId);
        if(itemOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // 게시글 존재 시 댓글 생성
        ReplyEntity replyEntity = new ReplyEntity();
        replyEntity.setItemId(itemId);
        replyEntity.setContent(dto.getContent());
        replyEntity.setWriter(dto.getWriter());
        replyEntity.setPassword(dto.getPassword());
        return ReplyDto.fromEntity(replyRepository.save(replyEntity));
    }

    /* 댓글 읽기
    1. 불러 올 댓글이 존재하는 지 확인
    2. 존재한다면 해당 id에 대한 댓글 내용 확인
    3. 댓글은 누구든지 열람할 수 있다.
    4. 페이지 단위로 조회 해야한다.
    */
    public Page<ReplyDto> readAllReply(Integer page, Integer limit){
        Pageable pageable = PageRequest.of(0, 25);
        Page<ReplyEntity> replyEntityPage = replyRepository.findAll(pageable);
        List<ReplyDto> replyDtoList = new ArrayList<>();
        for (ReplyEntity entity : replyEntityPage)
            replyDtoList.add(ReplyDto.fromEntity(entity));
        return replyEntityPage.map(ReplyDto::fromEntity);
    }


    /* 댓글 수정
    1. 수정할 댓글이 존재하는 지 확인
    2. 존재한다면 해당 id에 대한 댓글 내용 수정
    3. 댓글이 등록될 때 첨부했던 비밀번호 검증
    */
    public ReplyDto updateReply(Long itemId, Long id, ReplyDto dto) throws IllegalAccessException {
        // 물품 게시글이 존재하는 지 먼저 확인
        SalesItemEntity itemEntity = existsItem(itemId);
        // 물품 게시글이 존재한다면 댓글 작성자가 맞는지 검증
        ReplyEntity replyEntity = checkUser(id, dto.getWriter(), dto.getPassword());
        // 사용자와 비밀번호가 일치한다면 댓글 수정
        replyEntity.setContent(dto.getContent());
        return ReplyDto.fromEntity(replyRepository.save(replyEntity));
    }


    /* 댓글 삭제
    1. 삭제할 댓글이 존재하는 지 확인
    2. 존재한다면 해당 id에 대한 댓글 삭제, 레포에서 바로 deleteById를 이용하여 삭제
    3. 댓글이 등록될 때 첨부했던 비밀번호 검증
    */
    public void deleteReply(Long itemId, Long id, ReplyDto dto) throws IllegalAccessException {
        SalesItemEntity itemEntity = existsItem(itemId);
        ReplyEntity replyEntity = checkUser(id, dto.getWriter(), dto.getPassword());
        replyRepository.deleteById(id);
    }


    /* 사용자 일치를 판단하는 checkUser()
    - id, writer, password 입력 시 기존의 db의 회원 정보와 일치하는지 확인
    - id : 댓글의 id
    - writer : 댓글 작성자
    - password : 댓글 작성자가 첨부한 비밀번호
     */
    public ReplyEntity checkUser(Long id, String writer, String password) throws IllegalAccessException {
        Optional<ReplyEntity> optionalReply = replyRepository.findById(id);
        if(optionalReply.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        ReplyEntity entity = optionalReply.get();

        if(!entity.getWriter().equals(writer) || !entity.getPassword().equals(password))
            throw new IllegalAccessException("사용자 정보가 일치하지 않습니다.");
        return entity;
    }

    // 게시글 존재 확인
    public SalesItemEntity existsItem(Long itemId){
        Optional<SalesItemEntity> optionalItem = itemRepository.findById(itemId);
        if(optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return optionalItem.get();
    }
}
