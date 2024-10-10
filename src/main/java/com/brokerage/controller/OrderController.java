package com.brokerage.controller;

import com.brokerage.models.entity.Order;
import com.brokerage.models.request.CreateOrderRequest;
import com.brokerage.producer.EventProducer;
import com.brokerage.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final EventProducer eventProducer;

    public OrderController(OrderService orderService, EventProducer eventProducer) {
        this.orderService = orderService;
        this.eventProducer = eventProducer;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(
                request.getCustomerId(),
                request.getAssetName(),
                request.getOrderSide(),
                request.getSize(),
                request.getPrice()
        );

        eventProducer.publishCreateOrderEvent(order);

        return ResponseEntity.ok("Order created with ID: " + order.getOrderId());
    }
}
