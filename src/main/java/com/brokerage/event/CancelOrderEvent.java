package com.brokerage.event;

import java.util.UUID;

public record CancelOrderEvent(UUID eventId, UUID orderId, UUID customerId) {}
