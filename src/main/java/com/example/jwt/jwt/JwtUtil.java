package com.example.jwt.jwt;

import com.example.jwt.response.UserResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


// 토큰 생성, 토큰 정보 추출, 토큰 유효성 검사
@Service
@RequiredArgsConstructor
public class JwtUtil {

    // secretKey 설정
    private final String secretKey = "jwtTest";

    // 토큰 유효시간 설정
    private final Long tokenValidTime = 3 * 60 * 1000L;
    private final Long refreshTokenValidTime = 7 * 24 * 60 * 60 * 1000L;

    // 토큰 정보로부터 아이디 추출
    public String extractUserId(String token) throws Exception {
        return extractClaim(token, Claims::getSubject);
    }

    /// 토큰 정보로부터 토큰 유효시간 추출
    public Date extractExpiration(String token) throws Exception{
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws Exception{
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 토큰에서 회원 정보 추출
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    // 토큰 유효시간 확인
    public Boolean isTokenExpired(String token) throws Exception{
        return extractExpiration(token).before(new Date());
    }

    // 토큰 생성 전 단계
    public UserResponse.TokenDto generateToken(String userId) {
        Map<String, Object> claims = new HashMap<>();

        String token = createToken(claims, userId);
        String refreshToken = createRefreshToken(claims, userId);

        return (new UserResponse.TokenDto(token, refreshToken));
    }

    // 토큰 생성
    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .setClaims(claims).setSubject(subject) // 정보 저장
                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 발행 시간 저장
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidTime)) // 토큰 유효 시간 저장
                .signWith(SignatureAlgorithm.HS256, secretKey).compact(); // 사용할 알고리즘과 secretKey 값을 세팅
    }

    // 리프레시 토큰 생성
    private String createRefreshToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims).setSubject(subject) // 정보 저장
                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 발행 시간 저장
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidTime)) // 토큰 유효 시간 저장
                .signWith(SignatureAlgorithm.HS256, secretKey).compact(); // 사용할 알고리즘과 secretKey 값을 세팅
    }

    // 토큰 유효성 검사
    public Boolean validateToken(String token, UserDetails userDetails) throws Exception {

        final String userId = extractUserId(token);
        return (userId.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
