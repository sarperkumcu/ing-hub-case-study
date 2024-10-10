package com.brokerage.service;

import com.brokerage.models.entity.Order;

import java.math.BigDecimal;
import java.util.UUID;

public interface OrderService {
    Order createOrder(UUID customerId, String assetName, String orderSide, BigDecimal size, BigDecimal price);
    }
