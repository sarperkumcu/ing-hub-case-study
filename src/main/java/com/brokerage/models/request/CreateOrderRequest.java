package com.brokerage.models.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateOrderRequest {
    private UUID customerId;
    private String assetName;
    private String orderSide;
    private BigDecimal size;
    private BigDecimal price;
}
