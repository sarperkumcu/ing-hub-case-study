package com.brokerage.service;

import com.brokerage.models.entity.Asset;
import com.brokerage.models.entity.Order;
import com.brokerage.repository.AssetRepository;
import com.brokerage.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;

    public OrderService(OrderRepository orderRepository, AssetRepository assetRepository) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
    }

    public Order createOrder(UUID customerId, String assetName, String orderSide, int size, double price) {
        Optional<Asset> assetOpt = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY");
        double requiredAmount = size * price;
        if ("BUY".equals(orderSide) && assetOpt.isPresent() && assetOpt.get().getUsableSize() >= requiredAmount) {
            Asset tryAsset = assetOpt.get();
            tryAsset.setUsableSize(tryAsset.getUsableSize() - requiredAmount);
            assetRepository.save(tryAsset);
        } else {
            throw new IllegalStateException("Insufficient balance for order");
        }

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setAssetName(assetName);
        order.setOrderSide(orderSide);
        order.setSize(size);
        order.setPrice(price);
        order.setStatus("PENDING");
        return orderRepository.save(order);
    }
}
