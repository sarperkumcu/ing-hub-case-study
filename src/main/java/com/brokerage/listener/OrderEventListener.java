package com.brokerage.listener;

import com.brokerage.event.*;
import com.brokerage.models.entity.Order;
import com.brokerage.models.entity.Asset;
import com.brokerage.repository.OrderRepository;
import com.brokerage.repository.AssetRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OrderEventListener {
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;

    public OrderEventListener(OrderRepository orderRepository, AssetRepository assetRepository) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
    }

    @KafkaListener(topics = "create-order-topic", groupId = "brokerage-group")
    @Transactional
    public void handleCreateOrder(CreateOrderEvent event) {
        // Validate if there is enough TRY or asset size for the order
        Optional<Asset> tryAsset = assetRepository.findByCustomerIdAndAssetName(event.customerId(), "TRY");
        if ("BUY".equals(event.orderSide()) && tryAsset.isPresent()) {
            double requiredAmount = event.size() * event.price();
            if (tryAsset.get().getUsableSize() >= requiredAmount) {
                // Update TRY balance and create order
                tryAsset.get().setUsableSize(tryAsset.get().getUsableSize() - requiredAmount);
                assetRepository.save(tryAsset.get());

                Order order = new Order();
                order.setOrderId(event.orderId());
                order.setCustomerId(event.customerId());
                order.setAssetName(event.assetName());
                order.setOrderSide(event.orderSide());
                order.setSize(event.size());
                order.setPrice(event.price());
                order.setStatus("PENDING");
                orderRepository.save(order);
            }
        }
    }


}
