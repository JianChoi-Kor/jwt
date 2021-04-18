package com.example.jwt.controller;

import com.example.jwt.dto.UserDto;
import com.example.jwt.entity.TokenEntity;
import com.example.jwt.entity.UserEntity;
import com.example.jwt.file.FileUtil;
import com.example.jwt.jwt.JwtUtil;
import com.example.jwt.repository.TokenRepository;
import com.example.jwt.repository.UserRepositorySupport;
import com.example.jwt.response.UserResponse;
import com.example.jwt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final UserRepositorySupport userRepositorySupport;
    private final FileUtil fileUtil;


    @GetMapping("/")
    public ResponseEntity<String> tokenTest() {
        return ResponseEntity.ok("test");
    }


    @GetMapping("/info")
    public ResponseEntity<UserResponse.UserInfoDto> getUserInfo(HttpServletRequest httpServletRequest) throws Exception {

        String authorizationHeader = httpServletRequest.getHeader("authorization");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtUtil.extractUserId(accessToken);

        return ResponseEntity.ok(userRepositorySupport.getUserInfo(userId));
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
            httpServletResponse.setHeader("Authorization", tokenInfo.getAccessToken());
            return ResponseEntity.ok(tokenInfo);
        }

        // 기존의 토큰이 없는 경우
        System.out.println("기존에 발급받은 토큰이 없는 경우");
        UserResponse.TokenDto tokens = jwtUtil.generateToken(loginDto.getUserId());
        UserEntity user = userService.findByUserId(loginDto.getUserId());

        httpServletResponse.setHeader("Authorization", tokens.getAccessToken());
        tokenRepository.save(new TokenEntity(user.getId(), tokens.getAccessToken(), tokens.getRefreshToken()));


        return ResponseEntity.ok(new UserResponse.LoginResponse(user.getId(), tokens.getAccessToken(), tokens.getRefreshToken()));
    }


    @PostMapping("/sign")
    public ResponseEntity<UserEntity> sign(UserDto.SignDto signDto) {
        return ResponseEntity.ok(userService.signUp(signDto));
    }


    @PostMapping("/uploadProfile")
    @ResponseStatus(HttpStatus.CREATED)
    public List<String> uploadProfile(@RequestParam List<MultipartFile> files, HttpServletRequest httpServletRequest) throws Exception {

        // Header accessToken으로 부터 userId를 뽑아오는 코드, 자주 쓰인다면 따로 메소드로 뺴는 것이 좋을 듯
        String authorizationHeader = httpServletRequest.getHeader("authorization");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtUtil.extractUserId(accessToken);

        String rootPath = FileSystemView.getFileSystemView().getHomeDirectory().toString();
        String basePath = rootPath + "/" + userId;

        fileUtil.makeFolders(basePath);

        List<String> list = new ArrayList<>();
        for(MultipartFile file : files) {
            String originalFileName = file.getOriginalFilename();
            File dest = new File(basePath, originalFileName);
            file.transferTo(dest);

            list.add(basePath + "/" + originalFileName);
        }
        return list;

    }

}
