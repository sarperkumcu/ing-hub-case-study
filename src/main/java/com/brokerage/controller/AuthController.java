package com.brokerage.controller;

import com.brokerage.JwtHelper;
import com.brokerage.models.entity.User;
import com.brokerage.models.request.LoginRequest;
import com.brokerage.models.request.RegisterRequest;
import com.brokerage.models.response.LoginResponse;
import com.brokerage.service.interfaces.AuthService;
import com.brokerage.service.UserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private AuthService authService;
    private AuthenticationManager authenticationManager;
    private UserDetailsServiceImpl userDetailsService;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService){
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> signup(@RequestBody RegisterRequest signupRequest) {
        authService.register(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userDetailsService.getUserByEmail(request.getEmail());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = JwtHelper.generateToken(user.getId().toString(), user.getEmail());
        return ResponseEntity.ok(new LoginResponse(request.getEmail(), token));
    }

}

