package com.example.jwt.service;

import com.example.jwt.dto.UserDto;
import com.example.jwt.entity.UserEntity;
import com.example.jwt.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntity signUp(UserDto.SignDto signDto) {
        if (userRepository.findByUserId(signDto.getUserId()) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }
        UserEntity user = UserEntity.builder()
                .userId(signDto.getUserId())
                .password(passwordEncoder.encode(signDto.getPassword()))
                .userName(signDto.getUserName())
                .email(signDto.getEmail())
                .build();

        return userRepository.save(user);
    }

    public UserEntity findByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }
}
