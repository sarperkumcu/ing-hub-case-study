package com.brokerage.repository;

import com.brokerage.models.entity.Asset;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {
    List<Asset> findByCustomerId(UUID customerId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Asset a WHERE a.customerId = :customerId AND a.assetName = :assetName")
    Optional<Asset> findByCustomerIdAndAssetNameForUpdate(@Param("customerId") UUID customerId, @Param("assetName") String assetName);

    Optional<Asset> findByCustomerIdAndAssetName(UUID customerId, String assetName);
}
