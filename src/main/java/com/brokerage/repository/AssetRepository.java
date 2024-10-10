package com.brokerage.repository;

import com.brokerage.models.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {
    List<Asset> findByCustomerId(UUID customerId);
    Optional<Asset> findByCustomerIdAndAssetName(UUID customerId, String assetName);
}
