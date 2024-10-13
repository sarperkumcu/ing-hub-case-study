package com.brokerage.event;

import com.brokerage.models.entity.User;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositEvent(UUID eventId, UUID userId, BigDecimal amount) {}
