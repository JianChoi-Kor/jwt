package com.example.jwt.response;

import lombok.Getter;
import lombok.Setter;

public class UserResponse {

    @Getter
    @Setter
    public static class TokenDto {
        private String token;
        private String refreshToken;
    }
}
