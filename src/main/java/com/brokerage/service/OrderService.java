package com.brokerage.service;

import com.brokerage.models.entity.Order;
import com.brokerage.models.request.CancelOrderRequest;
import com.brokerage.models.request.CreateOrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    UUID publishCreateOrderEvent(CreateOrderRequest createOrderRequest);
    UUID publishCancelOrderEvent(CancelOrderRequest cancelOrderRequest);
    Order createOrder(UUID customerId, String assetName, String orderSide, BigDecimal size, BigDecimal price);
    Order cancelOrder(UUID orderId, UUID customerId);
    Page<Order> getOrders(UUID customerId, LocalDateTime startDate, LocalDateTime endDate, String assetName, String orderSide, String status, Pageable pageable);
}
