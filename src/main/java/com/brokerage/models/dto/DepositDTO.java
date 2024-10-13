package com.brokerage.models.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositDTO(UUID userId, BigDecimal amount){}
