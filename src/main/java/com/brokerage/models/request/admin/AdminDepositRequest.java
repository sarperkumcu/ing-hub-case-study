package com.brokerage.models.request.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AdminDepositRequest {
    private UUID userId;
    private BigDecimal amount;
}
