package com.brokerage.service;

import com.brokerage.models.entity.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AssetService {
    Page<Asset> getAssets(UUID customerId, String assetName, Pageable pageable);

}
