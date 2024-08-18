package com.example.usedmarket.security;


import com.example.usedmarket.entity.Role;
import com.example.usedmarket.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.*;

import java.util.Collection;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    @Getter
    private Long id;
    private String username;
    private String password;
    @Getter
    private String email;
    @Getter
    private String phone;

    private List<GrantedAuthority> authorities;

    @Override
    // 계정이 갖고있는 권한 목록을 리턴한다.
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    // 계정의 비밀번호 리턴
    public String getPassword() {
        return this.password;
    }

    @Override
    // 계정의 이름을 리턴한다.
    public String getUsername() {
        return this.username;
    }

    @Override
    // 계정이 만료되었는지 리턴한다. ( true : 만료안됨 )
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    // 계정이 잠겨있지 않았는지 리턴한다. ( true : 잠기지 않음 )
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    // 비밀번호가 만료되지 않았는 지 리턴한다. (true: 만료안됨)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    // 계정이 활성화(사용가능)인 지 리턴한다. (true: 활성화)
    public boolean isEnabled() {
        return true;
    }


    /*
    UserEntity 에 설정된 Role 을 기준으로 권한 정보를 들고 있을 수 있도록 설정
    */
    public static CustomUserDetails fromEntity(UserEntity entity) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role: entity.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(String.format("ROLE_%s", role.getName())));
            authorities.addAll(role.getAuthorities()
                    .stream()
                    .map(p -> new SimpleGrantedAuthority(p.getName()))
                    .toList());
        }

        return CustomUserDetails.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .authorities(authorities)
                .build();
    }

    public UserEntity newEntity() {
        UserEntity entity = new UserEntity();
        entity.setUsername(username);
        entity.setPassword(password);
        return entity;
    }

    @Override
    public String toString() {
        return "CustomUserDetails{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
