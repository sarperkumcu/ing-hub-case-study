package com.brokerage.models.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateOrderDTO(UUID userId, String assetName, String orderSide, BigDecimal size, BigDecimal price){}