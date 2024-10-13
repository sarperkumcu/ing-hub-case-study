package com.brokerage.event;

import com.brokerage.models.entity.User;

import java.util.UUID;

public record CancelOrderEvent(UUID eventId, UUID orderId, UUID userId) {}
