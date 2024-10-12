package com.brokerage.models.request;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WithdrawRequest {
    private UUID customerId;
    private BigDecimal amount;
    private String iban;
}

