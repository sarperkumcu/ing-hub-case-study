package com.brokerage.controller;

import com.brokerage.auth.JwtHelper;
import com.brokerage.models.entity.User;
import com.brokerage.models.request.LoginRequest;
import com.brokerage.models.request.RegisterRequest;
import com.brokerage.models.response.LoginResponse;
import com.brokerage.models.response.RegisterResponse;
import com.brokerage.service.interfaces.AuthService;
import com.brokerage.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> signup(@Valid @RequestBody RegisterRequest signupRequest) {
        String token = authService.register(signupRequest);
        return ResponseEntity.ok(new RegisterResponse(token));
    }
    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        String token = authService.login(loginRequest);
        return ResponseEntity.ok(new LoginResponse(token));
    }

}

