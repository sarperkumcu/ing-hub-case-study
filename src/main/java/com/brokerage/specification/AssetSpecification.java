package com.brokerage.specification;


import com.brokerage.models.entity.Asset;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public class AssetSpecification {

    public static Specification<Asset> customerIdEquals(UUID customerId) {
        return (root, query, builder) -> builder.equal(root.get("customerId"), customerId);
    }

    public static Specification<Asset> assetNameEquals(String assetName) {
        return (root, query, builder) -> builder.equal(root.get("assetName"), assetName);
    }
}

