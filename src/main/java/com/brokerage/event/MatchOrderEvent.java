package com.brokerage.event;

import java.math.BigDecimal;
import java.util.UUID;

public record MatchOrderEvent(UUID eventId, UUID orderId) {}

