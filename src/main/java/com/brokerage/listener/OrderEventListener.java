package com.brokerage.listener;

import com.brokerage.event.*;
import com.brokerage.models.entity.Order;
import com.brokerage.models.entity.Asset;
import com.brokerage.repository.OrderRepository;
import com.brokerage.repository.AssetRepository;
import com.brokerage.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OrderEventListener {
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final OrderService orderService;
    public OrderEventListener(ObjectMapper objectMapper, OrderRepository orderRepository, AssetRepository assetRepository, OrderService orderService) {
        this.objectMapper = objectMapper;
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
        this.orderService = orderService;
    }

    @KafkaListener(topics = "create-order-topic", groupId = "brokerage-group")
    @Transactional
    public void handleCreateOrder(String event) {
        try {
            Order order = objectMapper.readValue(event, Order.class);
            orderService.createOrder(order.getCustomerId(), order.getAssetName(), order.getOrderSide(), order.getSize(), order.getPrice());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }


}
