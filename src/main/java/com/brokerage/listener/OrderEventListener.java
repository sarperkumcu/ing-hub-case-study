package com.brokerage.listener;

import com.brokerage.event.*;
import com.brokerage.exception.InsufficientBalanceException;
import com.brokerage.service.interfaces.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OrderEventListener {
    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    public OrderEventListener(ObjectMapper objectMapper, OrderService orderService) {
        this.objectMapper = objectMapper;
        this.orderService = orderService;
    }

    @RetryableTopic(
            attempts = "3",
            exclude = {InsufficientBalanceException.class, IllegalArgumentException.class},
            autoCreateTopics = "false"
    )
    @KafkaListener(topics = "create-order-topic", groupId = "brokerage-group")
    @Transactional
    public void handleCreateOrder(String event) {
        try {
            CreateOrderEvent order = objectMapper.readValue(event, CreateOrderEvent.class);
            orderService.createOrder(order.userId(), order.assetName(), order.orderSide(), order.size(), order.price());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @RetryableTopic(
            attempts = "3",
            exclude = {InsufficientBalanceException.class, IllegalArgumentException.class},
            autoCreateTopics = "false"
    )
    @KafkaListener(topics = "match-order-topic", groupId = "brokerage-group")
    @Transactional
    public void handleMatchOrder(String event) {
        try {
            MatchOrderEvent order = objectMapper.readValue(event, MatchOrderEvent.class);
            orderService.matchOrder(order.orderId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @RetryableTopic(
            attempts = "3",
            exclude = {IllegalArgumentException.class},
            autoCreateTopics = "false"
    )
    @KafkaListener(topics = "cancel-order-topic", groupId = "brokerage-group")
    @Transactional
    public void handleCancelOrder(String event) {
        try {
            log.info("OrderEventListener | HandleCancelOrder listener started: {}", event);
            CancelOrderEvent order = objectMapper.readValue(event, CancelOrderEvent.class);
            orderService.cancelOrder(order.orderId(), order.userId());
            log.info("OrderEventListener | HandleCancelOrder listener finished: {}", event);
        } catch (JsonProcessingException e) {
            log.info("OrderEventListener | HandleCancelOrder listener failed: {}", event);
            throw new RuntimeException(e);
        }

    }


}
