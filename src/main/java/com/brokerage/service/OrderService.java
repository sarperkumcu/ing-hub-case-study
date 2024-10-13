package com.brokerage.service;

import com.brokerage.models.dto.CancelOrderDTO;
import com.brokerage.models.dto.CreateOrderDTO;
import com.brokerage.models.entity.Order;
import com.brokerage.models.entity.User;
import com.brokerage.models.request.CancelOrderRequest;
import com.brokerage.models.request.CreateOrderRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    UUID publishCreateOrderEvent(CreateOrderDTO createOrderDTO);
    UUID publishCancelOrderEvent(CancelOrderDTO cancelOrderDTO);
    Order createOrder(UUID userId, String assetName, String orderSide, BigDecimal size, BigDecimal price);
    Order cancelOrder(UUID orderId, UUID customerId);
    Page<Order> getOrders(UUID customerId, LocalDateTime startDate, LocalDateTime endDate, String assetName, String orderSide, String status, Pageable pageable);
}
