package com.brokerage.service;

import com.brokerage.models.entity.User;
import com.brokerage.models.request.RegisterRequest;
import com.brokerage.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public void register(RegisterRequest request) {
        String email = request.getEmail();
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    @Override
    public void login(String email, String password) {

    }
}
