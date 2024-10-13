package com.brokerage.models.request.admin;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;


@Data
public class AdminCreateOrderRequest{
    @NotNull(message = "Order ID is required")
    private UUID userId;
    @NotBlank(message = "Asset name is required")
    private String assetName;

    @NotBlank(message = "Order side is required")
    private String orderSide;

    @NotNull(message = "Size is required")
    @DecimalMin(value = "0.01", message = "Size must be greater than 0")
    private BigDecimal size;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

}
