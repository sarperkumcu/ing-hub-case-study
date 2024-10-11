package com.brokerage.event;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositEvent(UUID eventId, UUID customerId, BigDecimal amount) {}
