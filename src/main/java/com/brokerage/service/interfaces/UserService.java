package com.brokerage.service.interfaces;

import com.brokerage.models.entity.User;

import java.util.UUID;

public interface UserService {
    User getUserById(UUID userId);

}