package com.brokerage.models.request;

import lombok.Data;
import java.util.UUID;

@Data
public class CreateOrderRequest {
    private UUID customerId;
    private String assetName;
    private String orderSide;
    private int size;
    private double price;
}
