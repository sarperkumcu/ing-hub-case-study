package com.brokerage.controller;

import com.brokerage.models.request.CancelOrderRequest;
import com.brokerage.models.request.CreateOrderRequest;
import com.brokerage.publisher.EventPublisher;
import com.brokerage.service.OrderServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderServiceImpl orderService;
    private final EventPublisher eventProducer;

    public OrderController(OrderServiceImpl orderService, EventPublisher eventProducer) {
        this.orderService = orderService;
        this.eventProducer = eventProducer;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestBody CreateOrderRequest request) {
        UUID orderId = eventProducer.publishCreateOrderEvent(request);
        return ResponseEntity.ok("Event created with ID: " + orderId);
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancelOrder(@RequestBody CancelOrderRequest request) {
        UUID orderId = eventProducer.publishCancelOrderEvent(request);
        return ResponseEntity.ok("Event created with ID: " + orderId);
    }
}
