package com.brokerage.models.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CancelOrderRequest {
    private UUID orderId;
}
