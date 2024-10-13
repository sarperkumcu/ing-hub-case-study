package com.brokerage.service.interfaces;

import com.brokerage.models.entity.Asset;
import com.brokerage.models.request.RegisterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AuthService {

    void register(RegisterRequest registerRequest);
    void login(String email, String password);


}
