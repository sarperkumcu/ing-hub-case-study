package com.brokerage.controller.admin;

import com.brokerage.models.dto.CancelOrderDTO;
import com.brokerage.models.dto.CreateOrderDTO;
import com.brokerage.models.entity.Order;
import com.brokerage.models.entity.User;
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
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PutMapping("/match/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> matchOrder(@PathVariable UUID orderId) {
        Order matchedOrder = orderService.matchPendingOrder(orderId);
        return ResponseEntity.ok("Order with ID: " + matchedOrder.getId() + " has been successfully matched.");
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createOrderAdmin(@Valid @RequestBody AdminCreateOrderRequest request) {
        CreateOrderDTO createOrderDTO = new CreateOrderDTO(request.getUserId(), request.getAssetName(), request.getOrderSide(), request.getSize(), request.getPrice());
        UUID orderId = orderService.publishCreateOrderEvent(createOrderDTO);
        return ResponseEntity.ok("Event created with ID: " + orderId);
    }

    @DeleteMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cancelOrderAdmin(@Valid @RequestBody AdminCancelOrderRequest request) {
        CancelOrderDTO cancelOrderDTO = new CancelOrderDTO(request.getUserId(), request.getOrderId());
        UUID orderId = orderService.publishCancelOrderEvent(cancelOrderDTO);
        return ResponseEntity.ok("Event created with ID: " + orderId);
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GetOrdersResponse>> listOrdersAdmin(
            @RequestParam UUID customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String assetName,
            @RequestParam(required = false) String orderSide,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Order> orders = orderService.getOrders(customerId, startDate, endDate, assetName, orderSide, status, pageable).getContent();
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