package com.brokerage.service;

import com.brokerage.auth.JwtHelper;
import com.brokerage.models.entity.User;
import com.brokerage.models.request.LoginRequest;
import com.brokerage.models.request.RegisterRequest;
import com.brokerage.repository.UserRepository;
import com.brokerage.service.interfaces.AuthService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;


    public AuthServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public String register(RegisterRequest request) {
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);
        userRepository.save(user);
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        return JwtHelper.generateToken(user.getId().toString(), user.getEmail());

    }

    @Override
    public String login(LoginRequest loginRequest) {
        User user = userDetailsService.getUserByEmail(loginRequest.getEmail());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        return JwtHelper.generateToken(user.getId().toString(), user.getEmail());
    }
}
