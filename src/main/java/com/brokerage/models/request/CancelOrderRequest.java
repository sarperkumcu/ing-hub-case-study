package com.brokerage.models.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CancelOrderRequest {
    @NotNull(message = "Order ID is required")
    private UUID orderId;
}
