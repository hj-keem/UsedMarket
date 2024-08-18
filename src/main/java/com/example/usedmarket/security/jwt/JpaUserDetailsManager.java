package com.example.usedmarket.security.jwt;


import com.example.usedmarket.entity.Authority;
import com.example.usedmarket.entity.Role;
import com.example.usedmarket.entity.UserEntity;
import com.example.usedmarket.repo.AuthorityRepo;
import com.example.usedmarket.repo.RoleRepo;
import com.example.usedmarket.repo.UserRepo;
import com.example.usedmarket.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@Service
// UserDetailsManager의 구현체로 만들면, Spring Security Filter에서 사용자 정보 회수에 활요할 수 있다.
public class JpaUserDetailsManager implements UserDetailsManager {
    private final UserRepo userRepository;
    private final RoleRepo roleRepository;
    private final AuthorityRepo authorityRepository;

    public JpaUserDetailsManager(
            UserRepo userRepository,
            PasswordEncoder passwordEncoder,
            RoleRepo roleRepository,
            AuthorityRepo authorityRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authorityRepository = authorityRepository;

        //권한
        // READ 권한 생성
        Authority readAuthority = new Authority();
        readAuthority.setName("READ");
        readAuthority = authorityRepository.save(readAuthority);

        // WRITE 권한 생성
        Authority writeAuthority = new Authority();
        writeAuthority.setName("WRITE");
        writeAuthority = authorityRepository.save(writeAuthority);

        // USER 역할 생성 (READ)
        Role userRole = new Role();
        userRole.setName("USER");
        userRole.getAuthorities().add(readAuthority);
        userRole = roleRepository.save(userRole);

        // ADMIN 역할 생성 (READ, WRITE)
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole.getAuthorities().add(readAuthority);
        adminRole.getAuthorities().add(writeAuthority);
        adminRole = roleRepository.save(adminRole);

        // USER 역할 사용자 생성
        UserEntity user = new UserEntity();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("asdf"));
        user.getRoles().add(userRole);
        userRepository.save(user);

        // ADMIN 역할 사용자 생성
        UserEntity admin = new UserEntity();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("asdf"));
        admin.getRoles().add(adminRole);
        userRepository.save(admin);

        // Comment User
        UserEntity user2 = new UserEntity();
        user2.setUsername("user2");
        user2.setPassword(passwordEncoder.encode("asdf"));
        user2.getRoles().add(adminRole);
        userRepository.save(user2);

        // NegoPage User
        UserEntity user3 = new UserEntity();
        user3.setUsername("user3");
        user3.setPassword(passwordEncoder.encode("asdf"));
        user3.getRoles().add(adminRole);
        userRepository.save(user3);
    }

    @Override
    // UserDetailsService.loadUserByUsername(String)
    // 실제로 Spring Security 내부에서 사용하는 반드시 구현해야 정상동작을 기대할 수 있는 메소드
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser
                = userRepository.findByUsername(username);
        if (optionalUser.isEmpty())
            throw new UsernameNotFoundException(username);
        return CustomUserDetails.fromEntity(optionalUser.get());
    }

    @Override
    // 새로운 사용자를 저장하는 메소드 (선택)
    public void createUser(UserDetails user) {
        log.info("try create user: {}", user.getUsername());
        // 사용자가 (이미) 있으면 생성할수 없다.
        if (this.userExists(user.getUsername()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        try {
            // UserEntity 생성
            UserEntity newUser = ((CustomUserDetails) user).newEntity();

            // 기본 역할 부여 (USER)
            Role userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default role USER not found"));
            newUser.getRoles().add(userRole);

            // 사용자 저장
            this.userRepository.save(newUser);
        } catch (ClassCastException e) {
            log.error("failed to cast to {}", CustomUserDetails.class);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    // 계정이름을 가진 사용자가 존재하는지 확인하는 메소드 (선택)
    public boolean userExists(String username) {
        log.info("check if user: {} exists", username);
        return this.userRepository.existsByUsername(username);
    }




    // 그 외 기능들
    @Override
    public void updateUser(UserDetails user) {}

    @Override
    public void deleteUser(String username) {}

    @Override
    public void changePassword(String oldPassword, String newPassword) {}

}
