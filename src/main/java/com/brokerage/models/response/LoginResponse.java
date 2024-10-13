package com.brokerage.models.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String email;
    private String token;

    public LoginResponse(String email, String token) {
        this.email = email;
        this.token = token;
    }
}