package com.example.usedmarket.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
// 사용자가 Header에 포함한 JWT를 해석하고,
// 그에 따라 사용자가 인증된 상태인지를 확인하는 용도
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final JpaUserDetailsManager userDetailsManager;

    public JwtTokenFilter(JwtTokenUtils jwtTokenUtils, JpaUserDetailsManager userDetailsManager) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.userDetailsManager = userDetailsManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // JWT가 포함되어 있으면 포함되어 있는 헤더를 요청
        String authHeader
                = request.getHeader(HttpHeaders.AUTHORIZATION);
        // authHeader가 null이 아니면서 "Bearer " 로 구성되어 있어야 정상적인 인증 정보.
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // JWT를 회수하여 JWT가 정상적인 JWT인지를 판단한다.
            String token = authHeader.split(" ")[1];
            if (jwtTokenUtils.validate(token)) {
                // 사용자가 Header 에 포함시킨 JWT Token 에 대한 인증을 끝내고 인증된 정보를 저장
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                // JWT에서 사용자 이름을 가져오기
                String username = jwtTokenUtils.parseClaims(token).getSubject();
                // 사용자 인증 정보 생성
                UserDetails userDetails = userDetailsManager.loadUserByUsername(username);
                log.info("여기");

                /*
                사용자 인증 토큰
                첫번째 인자는 사용자 정보를 나타내는 객체를 전달 (사용자 정보는 인증된 사용자의 정보)
                두번째 인자는 사용자의 비밀번호 또는 토큰 값을 전달
                세 번째 인자는 사용자의 권한 정보를 나타내는 객체를 전달
                */
                AbstractAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(
                        userDetails, // 사용자 정보
                        token, // 사용된 토큰
                        userDetails.getAuthorities() // 권한 정보 (Role)
                );
                // SecurityContext에 사용자 정보 설정
                context.setAuthentication(authenticationToken);
                // 인증된 사용자의 인증정보를 SecurityContextHolder에 설정
                SecurityContextHolder.setContext(context);
                log.info("set security context with jwt");
                log.info(SecurityContextHolder.getContext().getAuthentication().getName());
                log.info("Principal: {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
                log.info("Authorities: {}", SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            }
            // 아니라면 log.warn을 통해 알린다.
            else {
                log.warn("jwt validation failed");
            }
        }
        filterChain.doFilter(request, response);
    }
}
