package com.brokerage.models.request.admin;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AdminWithdrawRequest {
    private UUID userId;
    private BigDecimal amount;
    private String iban;
}

