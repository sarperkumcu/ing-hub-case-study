package com.brokerage.controller;

import com.brokerage.models.entity.Order;
import com.brokerage.models.request.CancelOrderRequest;
import com.brokerage.models.request.CreateOrderRequest;
import com.brokerage.publisher.OrderEventPublisher;
import com.brokerage.service.OrderService;
import com.brokerage.service.OrderServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService, OrderEventPublisher eventProducer) {
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
    @GetMapping("")
    public ResponseEntity<Page<Order>> listOrders(
            @RequestParam UUID customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String assetName,
            @RequestParam(required = false) String orderSide,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        // @todo map to response
        Page<Order> orders = orderService.getOrders(customerId, startDate, endDate, assetName, orderSide, status, pageable);
        return ResponseEntity.ok(orders);
    }
}
