package com.brokerage.models.request.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AdminCancelOrderRequest  {
    @NotNull(message = "User ID is required")
    private UUID userId;
    @NotNull(message = "Order ID is required")
    private UUID orderId;
}
