package com.example.jwt.controller;

import com.example.jwt.dto.UserDto;
import com.example.jwt.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String generateToken(UserDto.LoginDto loginDto) throws Exception {
        try {

            // 아이디와 패스워드를 Secutiry가 알아볼 수 있는 token 객체로 변환한다.
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginDto.getUserId(), loginDto.getPassword());

            // AuthenticationManager에 token을 넘기면 UserDetailsService가 받아 처리하도록 한다.
            Authentication authentication = authenticationManager.authenticate(token);

            // 실제 SecurityContext에 authentication 정보를 등록한다.
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            throw new Exception("inavalid userId, password");
        }
        return jwtUtil.generateToken(loginDto.getUserId());
    }
}
