package com.brokerage.service;


import com.brokerage.specification.AssetSpecification;
import com.brokerage.models.entity.Asset;
import com.brokerage.repository.AssetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class AssetServiceImpl implements AssetService{
    private final AssetRepository assetRepository;


    public AssetServiceImpl(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }
    @Override
    public Page<Asset> getAssets(UUID customerId,  String assetName, Pageable pageable) {
        Specification<Asset> spec = Specification.where(AssetSpecification.customerIdEquals(customerId));

        if (assetName != null && !assetName.isEmpty()) {
            spec = spec.and(AssetSpecification.assetNameEquals(assetName));
        }
        return assetRepository.findAll(spec, pageable);
    }
}
