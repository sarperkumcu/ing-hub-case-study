package com.brokerage.models.request.admin;

import com.brokerage.models.request.CreateOrderRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.UUID;


@Data
public class AdminCreateOrderRequest{
    private UUID userId;
    private String assetName;
    private String orderSide;
    private BigDecimal size;
    private BigDecimal price;

}
