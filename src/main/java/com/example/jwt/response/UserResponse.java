package com.example.jwt.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class UserResponse {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TokenDto {
        private String accessToken;
        private String refreshToken;
    }


    @Getter
    @Setter
    @AllArgsConstructor
    public static class LoginResponse {
       private Long userIdx;
       private String accessToken;
       private String refreshToken;
    }
}
