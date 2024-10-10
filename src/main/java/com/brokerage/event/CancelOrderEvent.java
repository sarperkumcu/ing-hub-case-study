package com.brokerage.event;

import java.util.UUID;

public record CancelOrderEvent(UUID orderId, UUID customerId) {}
