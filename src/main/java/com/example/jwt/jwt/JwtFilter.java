package com.example.jwt.jwt;

import com.example.jwt.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService service;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        // request에서 "Authorization" 이라는 이름의 header 정보를 받아온다.
        String authorizationHeader = httpServletRequest.getHeader("Authorization");

        String token = null;
        String userId = null;

        // 받아온 정보가 null이 아니고 + startsWith "Bearer "로 시작한다면 토큰 값과 userName을 추출
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            token = authorizationHeader.substring(7);
            userId = jwtUtil.extractUserId(token);
        }

       if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

           UserDetails userDetails = service.loadUserByUsername(userId);

           // validateToken method를 통해 토큰과 유저 정보가 일치하는지 확인
           if(jwtUtil.validateToken(token, userDetails)) {

               UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                       new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
               usernamePasswordAuthenticationToken
                       .setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

               //SecurityContext에 Authentication 객체를 저장
               SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
           }
       }
       filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
