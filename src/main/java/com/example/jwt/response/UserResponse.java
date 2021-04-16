package com.example.jwt.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.metamodel.StaticMetamodel;

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


    @Setter
    @Getter
    @AllArgsConstructor
    public static class UserInfoDto {
        private String userId;
        private String userName;
        private String email;
    }
}
