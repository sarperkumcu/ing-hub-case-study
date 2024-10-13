package com.brokerage.models.response;

import java.math.BigDecimal;
import java.util.UUID;

public record GetAssetsResponse(
        UUID id,
        String assetName,
        BigDecimal size,
        BigDecimal usableSize
) {}
