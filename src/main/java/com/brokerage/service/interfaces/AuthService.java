package com.brokerage.service.interfaces;

import com.brokerage.models.entity.Asset;
import com.brokerage.models.request.LoginRequest;
import com.brokerage.models.request.RegisterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AuthService {

    String register(RegisterRequest registerRequest);
    String login(LoginRequest loginRequest);


}
