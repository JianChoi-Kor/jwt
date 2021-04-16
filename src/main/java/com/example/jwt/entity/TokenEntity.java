package com.example.jwt.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class TokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_idx")
    private Long id;
    private Long userIdx;
    private String accessToken;
    private String refreshToken;

    public TokenEntity(Long id, Long userIdx, String accessToken, String refreshToken) {
        this.id = id;
        this.userIdx = userIdx;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public TokenEntity(Long userIdx, String accessToken, String refreshToken) {
        this.userIdx = userIdx;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
