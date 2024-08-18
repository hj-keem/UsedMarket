package com.example.usedmarket.security;

import com.example.usedmarket.security.jwt.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


/*
- .requestMatchers() : 인자로 전달받은 URL 값들에 대해서 설정
- .permitAll() : requestMatchers() 로 설정한 URL들이 인증이 없어도 접근이 가능하도록 설정
- AbstractHttpConfigurer : 보안 설정을 정의하는 HttpSecurity를 구성하는 데 사용
*/
@Configuration
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    public WebSecurityConfig(JwtTokenFilter jwtTokenFilter){
        this.jwtTokenFilter = jwtTokenFilter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .authorizeHttpRequests(
                        authHttp -> authHttp
                                .requestMatchers("/token/issue","token/secured")
                                .permitAll()
                                // ---------------------------------------- //
                                .requestMatchers(HttpMethod.POST, "/items/**")
                                .hasAnyRole("ADMIN","USER")
                                .requestMatchers(HttpMethod.GET,"/items/**")
                                .permitAll() //GET은 모두 허용
                                .requestMatchers(HttpMethod.PUT, "/items/**")
                                .hasAnyRole("USER")
                                .requestMatchers(HttpMethod.DELETE,"/items/**")
                                .hasAnyRole("ADMIN","USER")
                                // ---------------------------------------- //
                                .requestMatchers("/users/register")
                                .anonymous()  // 인증이 되지 않은 사용자만 허가
//                                .anyRequest()
//                                .authenticated()
                )
                // 인증상태 또는 세션상태를 저장하면 안되므로 세션 비저장으로 설정해준다.
                .sessionManagement(
                        sessionManagement -> sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 필터사용
                .addFilterBefore(
                        jwtTokenFilter,
                        AuthorizationFilter.class
                );
        return http.build();
    }
}
