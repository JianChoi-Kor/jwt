package com.example.jwt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_idx")
    private Long id;
    private Long userIdx;
    private String token;
    private String refreshToken;

    public TokenEntity(Long userIdx, String token, String refreshToken) {
        this.userIdx = userIdx;
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
