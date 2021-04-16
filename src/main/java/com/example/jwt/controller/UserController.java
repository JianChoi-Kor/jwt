package com.example.jwt.controller;

import com.example.jwt.dto.UserDto;
import com.example.jwt.entity.TokenEntity;
import com.example.jwt.entity.UserEntity;
import com.example.jwt.jwt.JwtUtil;
import com.example.jwt.repository.TokenRepository;
import com.example.jwt.repository.UserRepositorySupport;
import com.example.jwt.response.UserResponse;
import com.example.jwt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final UserRepositorySupport userRepositorySupport;


    @GetMapping("/")
    public ResponseEntity<String> tokenTest() {
        return ResponseEntity.ok("test");
    }



    @PostMapping("/login")
    public ResponseEntity<UserResponse.LoginResponse> generateToken(UserDto.LoginDto loginDto, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {

        try {
            // 아이디와 패스워드를 Secutiry가 알아볼 수 있는 token 객체로 변환한다.
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginDto.getUserId(), loginDto.getPassword());
            // AuthenticationManager에 token을 넘기면 UserDetailsService가 받아 처리하도록 한다.
            Authentication authentication = authenticationManager.authenticate(token);
        } catch (Exception e) {
            throw new Exception("아이디 비밀번호가 일치하지 않습니다.");
        }

        UserResponse.LoginResponse tokenInfo = userRepositorySupport.getTokenInfo(loginDto.getUserId());
        // 기존의 토큰이 있는 경우
        if(tokenInfo != null) {
            System.out.println("기존에 발급받은 토큰이 있는 경우");
            httpServletResponse.setHeader("authorization", tokenInfo.getAccessToken());
            return ResponseEntity.ok(tokenInfo);
        }

        // 기존의 토큰이 없는 경우
        System.out.println("기존에 발급받은 토큰이 없는 경우");
        UserResponse.TokenDto tokens = jwtUtil.generateToken(loginDto.getUserId());
        UserEntity user = userService.findByUserId(loginDto.getUserId());

        httpServletResponse.setHeader("authorization", tokens.getAccessToken());
        tokenRepository.save(new TokenEntity(user.getId(), tokens.getAccessToken(), tokens.getRefreshToken()));


        return ResponseEntity.ok(new UserResponse.LoginResponse(user.getId(), tokens.getAccessToken(), tokens.getRefreshToken()));
    }


    @PostMapping("/sign")
    public ResponseEntity<UserEntity> sign(UserDto.SignDto signDto) {
        return ResponseEntity.ok(userService.signUp(signDto));
    }
}
