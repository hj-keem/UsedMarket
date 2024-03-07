package com.example.usedmarket.service;

import com.example.usedmarket.dto.NegoDto;
import com.example.usedmarket.entity.NegoEntity;
import com.example.usedmarket.entity.SalesItemEntity;
import com.example.usedmarket.repo.NegoRepo;
import com.example.usedmarket.repo.SalesItemRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // final 생성자 생성
public class NegoService {
    private final NegoRepo negoRepository;
    private final SalesItemRepo itemRepository;

    // 제안 등록
    public NegoDto createNego(Long itemId, NegoDto dto) {
        SalesItemEntity itemEntity = itemCheck(itemId);
        NegoEntity negoEntity = new NegoEntity();
        negoEntity.setItemId(itemId);
        negoEntity.setSuggestedPrice(dto.getSuggestedPrice());
        negoEntity.setStatus(dto.getStatus());
        negoEntity.setWriter(dto.getWriter());
        negoEntity.setPassword(dto.getPassword());
        return NegoDto.fromEntity(negoRepository.save(negoEntity));
    }


    public Page<NegoDto> readNegoPage(Long itemId, Integer page, Integer limit) {
        SalesItemEntity itemEntity = itemCheck(itemId);
        Pageable pageable = PageRequest.of(0,25);
        Page<NegoEntity> negoEntityPage = negoRepository.findAll(pageable);
        List<NegoDto> negoDtoList = new ArrayList<>();
        for (NegoEntity entity : negoEntityPage)
            negoDtoList.add(NegoDto.fromEntity(entity));
        return negoEntityPage.map(NegoDto::fromEntity);
    }

    public NegoDto updateNego(Long itemId, Long id, NegoDto dto) throws IllegalAccessException {
        // 게시글이 존재하는 지 확인
        SalesItemEntity itemEntity = itemCheck(itemId);
        // 제안 id 존재 및 유저 검증
        NegoEntity negoEntity = userCheck(id, dto.getWriter(), dto.getPassword());
        // 다른 문제 없을 시 내용 수정
        negoEntity.setSuggestedPrice(dto.getSuggestedPrice());
        negoEntity.setStatus(dto.getStatus());
        // 수정된 내용 entity에 저장
        return NegoDto.fromEntity(negoRepository.save(negoEntity));
    }

    public void deleteNego(Long itemId, Long id, NegoDto dto) throws IllegalAccessException {
        SalesItemEntity itemEntity = itemCheck(itemId);
        NegoEntity negoEntity = userCheck(id, dto.getWriter(), dto.getPassword());
        negoRepository.deleteById(id);
    }

    // itmeId 검증
    public SalesItemEntity itemCheck(Long itemId){
        Optional<SalesItemEntity> optionalItem = itemRepository.findById(itemId);
        if(optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return optionalItem.get();
    }

    // 유저 검증
    public NegoEntity userCheck(Long id, String writer, String password ) throws IllegalAccessException {
        Optional<NegoEntity> optionalNego = negoRepository.findById(id);
        if(optionalNego.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        NegoEntity entity = optionalNego.get();
        if(!entity.getWriter().equals(writer) || !entity.getPassword().equals(password))
            throw new IllegalAccessException("사용자 정보가 일치하지 않습니다.");

        return entity;
    }
}
