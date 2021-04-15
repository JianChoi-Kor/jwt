package com.example.jwt.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class UserResponse {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TokenDto {
        private String token;
        private String refreshToken;
    }
}
