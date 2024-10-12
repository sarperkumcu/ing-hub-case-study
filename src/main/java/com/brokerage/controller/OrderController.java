package com.brokerage.controller;

import com.brokerage.models.request.CancelOrderRequest;
import com.brokerage.models.request.CreateOrderRequest;
import com.brokerage.publisher.OrderEventPublisher;
import com.brokerage.service.OrderServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderServiceImpl orderService;

    public OrderController(OrderServiceImpl orderService, OrderEventPublisher eventProducer) {
        this.orderService = orderService;
    }

    @PostMapping("")
    public ResponseEntity<String> createOrder(@RequestBody CreateOrderRequest request) {
        UUID orderId = orderService.publishCreateOrderEvent(request);
        return ResponseEntity.ok("Event created with ID: " + orderId);
    }

    @DeleteMapping("")
    public ResponseEntity<String> cancelOrder(@RequestBody CancelOrderRequest request) {
        UUID orderId = orderService.publishCancelOrderEvent(request);
        return ResponseEntity.ok("Event created with ID: " + orderId);
    }
}
