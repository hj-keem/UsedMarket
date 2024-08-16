package com.example.usedmarket.controller;

import com.example.usedmarket.dto.ResponseDto;
import com.example.usedmarket.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController  // 로그인 페이지를 보여줄려고
@RequestMapping("/users")
public class UserController {


    // 로그인 성공 후 로그인 여부를 판단하기 위한 GetMapping
    @GetMapping("/my-profile")
    public String myProfile(Authentication authentication) {
        log.info(authentication.getName());
        log.info(((User) authentication.getPrincipal()).getUsername());
        CustomUserDetails userDetails
                = (CustomUserDetails) authentication.getPrincipal();
        log.info(userDetails.getUsername());
        log.info(SecurityContextHolder.getContext().getAuthentication().getName());
        return "my-profile";
    }


    @GetMapping("/register")
    public String registerForm() {
        return "register-form";
    }

    // 어떻게 사용자를 관리하는지는
    // interface 기반으로 의존성 주입
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;


    public UserController(
            UserDetailsManager manager,
            PasswordEncoder passwordEncoder
    ) {
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseDto registerPost(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("password-check") String passwordCheck
    ) {
        ResponseDto response = new ResponseDto();
        if (password.equals(passwordCheck)) {
            manager.createUser(CustomUserDetails.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .build());
            response.setMessage("회원가입 성공!");
            return response;
        }
        response.setMessage("회원가입 실패");
        return response;
    }
}
