package com.brokerage.models.request.admin;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AdminWithdrawRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    @NotBlank(message = "IBAN is required")
    @Pattern(
            regexp = "^[A-Z]{2}\\d{2}[A-Z0-9]{12,30}$",
            message = "Invalid IBAN format"
    )
    private String iban;
}

