package com.brokerage;

import com.brokerage.models.entity.Order;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderSpecification {

    public static Specification<Order> customerIdEquals(UUID customerId) {
        return (root, query, builder) -> builder.equal(root.get("customerId"), customerId);
    }

    public static Specification<Order> createDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, builder) -> builder.between(root.get("createDate"), startDate, endDate);
    }

    public static Specification<Order> assetNameEquals(String assetName) {
        return (root, query, builder) -> builder.equal(root.get("assetName"), assetName);
    }

    public static Specification<Order> orderSideEquals(String orderSide) {
        return (root, query, builder) -> builder.equal(root.get("orderSide"), orderSide);
    }

    public static Specification<Order> statusEquals(String status) {
        return (root, query, builder) -> builder.equal(root.get("status"), status);
    }
}
