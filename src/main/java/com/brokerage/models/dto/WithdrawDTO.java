package com.brokerage.models.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record WithdrawDTO(UUID userId, BigDecimal amount, String iban){}

