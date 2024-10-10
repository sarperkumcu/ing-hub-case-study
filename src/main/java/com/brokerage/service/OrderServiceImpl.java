package com.brokerage.service;

import com.brokerage.models.entity.Asset;
import com.brokerage.models.entity.Order;
import com.brokerage.repository.AssetRepository;
import com.brokerage.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;

    public OrderServiceImpl(OrderRepository orderRepository, AssetRepository assetRepository) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
    }

    public Order createOrder(UUID customerId, String assetName, String orderSide, int size, BigDecimal price) {
        Optional<Asset> assetOpt = assetRepository.findByCustomerIdAndAssetName(customerId, "TRY");

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
