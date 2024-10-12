package com.brokerage.repository;

import com.brokerage.models.entity.Asset;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID>, JpaSpecificationExecutor<Asset> {
    List<Asset> findByUserId(UUID userId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Asset a WHERE a.user.id = :userId AND a.assetName = :assetName")
    Optional<Asset> findByUserIdAndAssetNameForUpdate(@Param("userId") UUID userId, @Param("assetName") String assetName);
    Optional<Asset> findByUserIdAndAssetName(UUID customerId, String assetName);
}
