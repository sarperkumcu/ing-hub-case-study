package com.brokerage.event;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderEvent(UUID eventId, UUID customerId, String assetName, String orderSide, int size, BigDecimal price) {}
