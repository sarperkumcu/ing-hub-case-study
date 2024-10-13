package com.brokerage.event;

import com.brokerage.models.entity.User;

import java.math.BigDecimal;
import java.util.UUID;

public record WithdrawEvent(UUID eventId, UUID userId, BigDecimal amount, String iban) {}

