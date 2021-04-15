package com.example.jwt.dto;

import lombok.Getter;
import lombok.Setter;

public class UserDto {

    @Getter
    @Setter
    public static class LoginDto {
        private String userId;
        private String password;
    }

    @Getter
    @Setter
    public static class SignDto {
        private String userId;
        private String password;
        private String userName;
        private String email;
    }
}
