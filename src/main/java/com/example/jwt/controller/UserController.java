package com.example.jwt.controller;

import com.example.jwt.dto.UserDto;
import com.example.jwt.entity.TokenEntity;
import com.example.jwt.entity.UserEntity;
import com.example.jwt.jwt.JwtUtil;
import com.example.jwt.repository.TokenRepository;
import com.example.jwt.repository.UserRepository;
import com.example.jwt.response.UserResponse;
import com.example.jwt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenRepository tokenRepository;

    @PostMapping("/login")
    public ResponseEntity<TokenEntity> generateToken(UserDto.LoginDto loginDto) throws Exception {
        try {
            // 아이디와 패스워드를 Secutiry가 알아볼 수 있는 token 객체로 변환한다.
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginDto.getUserId(), loginDto.getPassword());
            // AuthenticationManager에 token을 넘기면 UserDetailsService가 받아 처리하도록 한다.
            Authentication authentication = authenticationManager.authenticate(token);

        } catch (Exception e) {
            throw new Exception("아이디 비밀번호가 일치하지 않습니다.");
        }
        // 토큰 생성
        UserResponse.TokenDto tokens = jwtUtil.generateToken(loginDto.getUserId());
        UserEntity user = userService.findByUserId(loginDto.getUserId());
        System.out.println(user);

        return ResponseEntity.ok(tokenRepository.save(new TokenEntity(user.getId(), tokens.getToken(), tokens.getRefreshToken())));
    }

    @PostMapping("/sign")
    public ResponseEntity<UserEntity> sign(UserDto.SignDto signDto) {
        return ResponseEntity.ok(userService.signUp(signDto));
    }
}
