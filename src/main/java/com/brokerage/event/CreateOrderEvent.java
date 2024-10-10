package com.brokerage.event;

import java.util.UUID;

public record CreateOrderEvent(UUID orderId, UUID customerId, String assetName, String orderSide, int size, double price) {}
