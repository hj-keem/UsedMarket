package com.example.usedmarket.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
// JWT 관련 기능들을 넣어두기 위한 기능성 클래스
// JWT 토큰의 생성, 검증, 파싱 등을 담당하는 유틸리티 클래스
public class JwtTokenUtils {
    private final Key signingKey;
    private final JwtParser jwtParser;

    public JwtTokenUtils(
            // yml에서 jwt.secret 값을 주입받아 JWT 생성 및 검증에 사용되는 비밀 키를 초기화
            @Value("${jwt.secret}")
            String jwtSecret //JWT 서명에 사용되는 비밀 키
    ) {
        // 인코딩하여 byte 배열에 넣어준다.
        this.signingKey
                = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        // JWT 번역기 만들기
        this.jwtParser = Jwts
                .parserBuilder()
                .setSigningKey(this.signingKey)
                .build();
    }

    // 1. JWT가 유효한지 판단하는 메소드
    //    jjwt 라이브러리에서는 JWT를 해석하는 과정에서
    //    유효하지 않으면 예외가 발생
    public boolean validate(String token) {
        try {
            // 정당한 JWT면 true,
            // parseClaimsJws: 암호화된 JWT를 해석하기 위한 메소드
            jwtParser.parseClaimsJws(token);
            return true;
            // 정당하지 않은 JWT면 false
        } catch (Exception e) {
            log.warn("invalid jwt: {}", e.getClass());
            return false;
        }
    }

    // JWT를 인자로 받고, 그 JWT를 해석해서
    // 사용자 정보를 회수하는 메소드
    public Claims parseClaims(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }

    // 주어진 사용자 정보를 바탕으로 JWT를 문자열로 생성
    public String generateToken(UserDetails userDetails) {
        // Claims: JWT 페이로드에 담기는 정보의 단위를 Claim이라 부른다.
        //         Claims는 Claim들을 담기위한 Map의 상속 interface이므로 여러 클레임을 "키-값 쌍"으로 저장
        //         즉, Claims는 JWT에서 클레임들을 저장하고 관리하는 일종의 Map
        Claims jwtClaims = Jwts.claims()
                // 사용자 정보 등록
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(3600)));

        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(signingKey)
                .compact();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 기본적으로 사용자 비밀번호는 해독가능한 형태로 데이터베이스에
        // 저장되면 안된다. 그래서 기본적으로 비밀번호를 단방향 암호화 하는
        // 인코더를 사용한다.
        return new BCryptPasswordEncoder();
    }
}