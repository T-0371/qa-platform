package com.example.qa.dto.request;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String username;
    private String password;
    private String email;
    private String gender;
    private Integer age;
    private String phone;
    private String avatar;
    private String securityQuestion;
    private String securityAnswer;
}
