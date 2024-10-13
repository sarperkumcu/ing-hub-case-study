package com.brokerage.event;

import com.brokerage.models.entity.User;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderEvent(UUID eventId, UUID userId, String assetName, String orderSide, BigDecimal size, BigDecimal price) {}
