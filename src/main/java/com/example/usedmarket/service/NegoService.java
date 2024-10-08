package com.example.usedmarket.service;

import com.example.usedmarket.dto.NegoDto;
import com.example.usedmarket.dto.SalesItemDto;
import com.example.usedmarket.entity.NegoEntity;
import com.example.usedmarket.entity.SalesItemEntity;
import com.example.usedmarket.entity.UserEntity;
import com.example.usedmarket.repo.NegoRepo;
import com.example.usedmarket.repo.SalesItemRepo;
import com.example.usedmarket.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepo userRepository;

    // 제안 등록
    public NegoDto createNego(Long itemId, NegoDto dto) {
        // 현재 사용자의 인증 정보 가져오기
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findAllByUsername(auth);

        SalesItemEntity itemEntity = itemCheck(itemId);

        NegoEntity negoEntity = new NegoEntity();
        negoEntity.setItemId(itemId);
//        negoEntity.setSalesItem(itemEntity);
        negoEntity.setAddItem(itemEntity);
        negoEntity.setSuggestedPrice(dto.getSuggestedPrice());
        negoEntity.setStatus(dto.getStatus());
        negoEntity.setAddUser(user);
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
        // 현재 사용자의 인증 정보 가져오기
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findAllByUsername(auth);

        // 게시글, 제안 id 존재하는 지 확인
        SalesItemEntity itemEntity = itemCheck(itemId);
        NegoEntity negoEntity = negoCheck(id);

        // 물품 등록시 사용했던 계정과 일치하다면
        if (!user.equals(negoEntity.getAddUser()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        // 다른 문제 없을 시 내용 수정
        negoEntity.setSuggestedPrice(dto.getSuggestedPrice());
        // 수정된 내용 entity에 저장
        return NegoDto.fromEntity(negoRepository.save(negoEntity));
    }

    public void deleteNego(Long itemId, Long id) throws IllegalAccessException {
        // 현재 사용자의 인증 정보 가져오기
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findAllByUsername(auth);

        SalesItemEntity itemEntity = itemCheck(itemId);
        NegoEntity negoEntity = negoCheck(id);

        // 물품 등록시 사용했던 계정과 일치하다면
        if (!user.equals(negoEntity.getAddUser()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        negoRepository.deleteById(id);
    }


    /*
    대상 물품의 주인은 구매 제안을 수락 | 거절 할 수 있다.
    1. 이를 위해서 제안의 대상 물품을 등록할 때 사용한 **작성자와 비밀번호**를 첨부해야 한다.
    2. 이때 구매 제안의 상태는 **수락** | **거절** 이 된다.
    */
    public NegoDto acceptOrReject(Long itemId, Long id, NegoDto dto) throws IllegalAccessException {
        // 현재 사용자의 인증 정보 가져오기
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findAllByUsername(auth);

        // 제안 대상 물품, 제안 id 찾기
        SalesItemEntity itemEntity = itemCheck(itemId);
        NegoEntity negoEntity = negoCheck(id);

        // 물품을 등록한 사용자 계정인 것이 인증되면 수락 | 거절 가능
        if(!user.equals(itemEntity.getAddUser()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        // 물품 등록자는 구매 제안 상태 변경 가능 (수락 | 거절)
        negoEntity.setStatus(dto.getStatus());
        return NegoDto.fromEntity(negoRepository.save(negoEntity));
    }

    // 구매 제안자는 상태가 '수락'일 경우 구매확정을 할 수 있다.
    // 제안 등록 시 사용했던 작성자와 비밀번호 첨부
    // 제안의 상태는(status) '확정' 으로 변경 -> '판매완료' 상태가 된다.
    // 구매 제안이 확정될 경우, 확정되지 않은 다른 구매 제의 상태는 모두 거절이 된다.
    public NegoDto purchaseConfirm(Long itemId, Long id, NegoDto dto) throws IllegalAccessException {
        // 현재 사용자의 인증 정보 가져오기
        String auth = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findAllByUsername(auth);

        SalesItemEntity itemEntity = itemCheck(itemId);
        NegoEntity negoEntity = negoCheck(id);

        // 제안 등록 시 사용했던 작성자와 비밀번호가 일치한다면 다음 로직 수행
        if(!user.equals(negoEntity.getAddUser()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        // 수락일 때 : 제안 등록자는 '확정'을 할 수 있다.
        if(negoEntity.getStatus().equals("수락"))
            negoEntity.setStatus(dto.getStatus());
        // 구매 확정 상태일 때 : 상태는 '판매 완료' 으로 바뀌게 된다.
        if(negoEntity.getStatus().equals("확정")){
            negoEntity.setStatus("판매 완료");
            itemEntity.setStatus("판매 완료");
            negoRepository.save(negoEntity);
            // 구매 확정 시 다른 제안 거절
            if(negoEntity.getStatus().equals("판매 완료")){
                List<NegoEntity> negotiationEntities = negoRepository.findByItemId(itemId);
                for (NegoEntity entity : negotiationEntities) {
                    if (!entity.getId().equals(id)) {
                        entity.setStatus("거절");
                        negoRepository.save(entity);
                    }
                }
            }
        }
        return NegoDto.fromEntity(negoRepository.save(negoEntity));
    }

    // itmeId 검증
    public SalesItemEntity itemCheck(Long itemId){
        Optional<SalesItemEntity> optionalItem = itemRepository.findById(itemId);
        if(optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return optionalItem.get();
    }

    // 제안 id 찾기
    public NegoEntity negoCheck(Long id){
        Optional<NegoEntity> optionalNego = negoRepository.findById(id);
        if(optionalNego.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return optionalNego.get();
    }

    // 제안 유저 검증
//    public NegoEntity userCheck(Long id, String writer, String password ) throws IllegalAccessException {
//        Optional<NegoEntity> optionalNego = negoRepository.findById(id);
//        if(optionalNego.isEmpty())
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//
//        NegoEntity entity = optionalNego.get();
//        if(!entity.getWriter().equals(writer) || !entity.getPassword().equals(password))
//            throw new IllegalAccessException("사용자 정보가 일치하지 않습니다.");
//
//        return entity;
//    }

    // 물품 유저 검증
//    public SalesItemEntity itemUserCheck(Long itemId, String writer, String password) throws IllegalAccessException {
//        Optional<SalesItemEntity> optionalItem = itemRepository.findById(itemId);
//        if(optionalItem.isEmpty())
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        SalesItemEntity entity = optionalItem.get();
//        if(!entity.getWriter().equals(writer) || !entity.getPassword().equals(password))
//            throw new IllegalAccessException("물품 등록자 정보와 일치하지 않습니다.");
//        return entity;
//    }
}
