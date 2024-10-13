package com.brokerage.models.dto;

import java.util.UUID;

public record CancelOrderDTO(UUID userId, UUID orderId){}
