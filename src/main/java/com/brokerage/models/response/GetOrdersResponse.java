package com.brokerage.models.response;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record GetOrdersResponse(
        UUID orderId,
        String assetName,
        String orderSide,
        BigDecimal size,
        BigDecimal price,
        String status,
        LocalDateTime createDate
) {}

