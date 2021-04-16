package com.example.jwt.jwt;

import com.example.jwt.entity.TokenEntity;
import com.example.jwt.entity.UserEntity;
import com.example.jwt.repository.TokenRepository;
import com.example.jwt.repository.UserRepositorySupport;
import com.example.jwt.response.UserResponse;
import com.example.jwt.service.CustomUserDetailsService;
import com.example.jwt.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
@WebFilter("/")
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService service;
    private final UserRepositorySupport userRepositorySupport;
    private final TokenRepository tokenRepository;
    private final UserService userService;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("jwt 필터 실행 여부 확인");

        // request에서 "Authorization" 이라는 이름의 header 정보를 받아온다.
        String authorizationHeader = httpServletRequest.getHeader("authorization");

        String accessToken = null;
        String userId = null;

        // 받아온 정보가 null이 아니고 + startsWith "Bearer "로 시작한다면 토큰 값과 userId 추출
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            accessToken = authorizationHeader.substring(7);
            try {
                userId = jwtUtil.extractUserId(accessToken);
            } catch (ExpiredJwtException e){
                // 토큰 유효성 체크해서 다시 저장
                    System.out.println("accessToken 만료");

                    TokenEntity tokenInfo = userRepositorySupport.getRefreshToken(accessToken);

                    userId = jwtUtil.extractUserId(tokenInfo.getRefreshToken());

                    if(!jwtUtil.isTokenExpired(tokenInfo.getRefreshToken())) {
                        System.out.println("토큰 재발급, 저장");
                        UserResponse.TokenDto newTokens = jwtUtil.generateToken(userId);
                        UserEntity userEntity = userService.findByUserId(userId);

                        httpServletResponse.setHeader("authorization", newTokens.getAccessToken());
                        tokenRepository.save(new TokenEntity(tokenInfo.getId(), userEntity.getId(), newTokens.getAccessToken(), newTokens.getRefreshToken()));
                    }
            }

        }



       if (userId != null) {
           UserDetails userDetails = service.loadUserByUsername(userId);
           System.out.println("userDetails" + userDetails);

           // validateToken method를 통해 토큰의 유효성 을 확인
           if(jwtUtil.validateToken(accessToken, userDetails)) {

               UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                       new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
               usernamePasswordAuthenticationToken
                       .setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
           }
       }

       filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
