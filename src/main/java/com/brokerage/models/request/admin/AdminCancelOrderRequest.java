package com.brokerage.models.request.admin;

import com.brokerage.models.request.CancelOrderRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
public class AdminCancelOrderRequest  {
    private UUID userId;
    private UUID orderId;
}
