package com.brokerage.controller;

import com.brokerage.models.dto.CancelOrderDTO;
import com.brokerage.models.dto.CreateOrderDTO;
import com.brokerage.models.entity.Order;
import com.brokerage.models.entity.User;
import com.brokerage.models.request.CancelOrderRequest;
import com.brokerage.models.request.CreateOrderRequest;
import com.brokerage.models.request.admin.AdminCancelOrderRequest;
import com.brokerage.models.request.admin.AdminCreateOrderRequest;
import com.brokerage.models.response.GetOrdersResponse;
import com.brokerage.service.interfaces.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping("")
    public ResponseEntity<String> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        CreateOrderDTO createOrderDTO = new CreateOrderDTO(user.getId(), request.getAssetName(), request.getOrderSide(), request.getSize(), request.getPrice());
        UUID orderId = orderService.publishCreateOrderEvent(createOrderDTO);
        return ResponseEntity.ok("Event created with ID: " + orderId);
    }



    @DeleteMapping("")
    public ResponseEntity<String> cancelOrder(@Valid @RequestBody CancelOrderRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        CancelOrderDTO cancelOrderDTO = new CancelOrderDTO(user.getId(), request.getOrderId());
        UUID orderId = orderService.publishCancelOrderEvent(cancelOrderDTO);
        return ResponseEntity.ok("Event created with ID: " + orderId);
    }

    @GetMapping("")
    public ResponseEntity<List<GetOrdersResponse>> listOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String assetName,
            @RequestParam(required = false) String orderSide,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        List<Order> orders = orderService.getOrders(user.getId(), startDate, endDate, assetName, orderSide, status, pageable).getContent();
        List<GetOrdersResponse> orderResponses = orders.stream()
                .map(order -> new GetOrdersResponse(
                        order.getId(),
                        order.getAssetName(),
                        order.getOrderSide().name(),
                        order.getSize(),
                        order.getPrice(),
                        order.getStatus().name(),
                        order.getCreateDate()
                ))
                .toList();
        return ResponseEntity.ok(orderResponses);
    }
}
