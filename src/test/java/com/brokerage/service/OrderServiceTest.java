package com.brokerage.service;

import com.brokerage.exception.InsufficientBalanceException;
import com.brokerage.exception.ResourceNotFoundException;
import com.brokerage.models.entity.Asset;
import com.brokerage.models.entity.Order;
import com.brokerage.models.entity.User;
import com.brokerage.models.enums.OrderSide;
import com.brokerage.models.enums.OrderStatus;
import com.brokerage.publisher.OrderEventPublisher;
import com.brokerage.repository.AssetRepository;
import com.brokerage.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private OrderEventPublisher eventPublisher;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID orderId;
    private UUID customerId;
    private Order pendingOrder;
    private User user;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        user = new User();
        user.setId(customerId);

        pendingOrder = new Order();
        pendingOrder.setId(orderId);
        pendingOrder.setUser(user);
        pendingOrder.setAssetName("APPL");
        pendingOrder.setOrderSide(OrderSide.BUY);
        pendingOrder.setSize(BigDecimal.valueOf(10));
        pendingOrder.setPrice(BigDecimal.valueOf(100));
        pendingOrder.setStatus(OrderStatus.PENDING);
    }

    @Test
    void cancelOrder_SuccessfulCancellation() {
        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.of(pendingOrder));

        Asset tryAsset = new Asset();
        tryAsset.setUsableSize(BigDecimal.valueOf(2000));
        when(assetRepository.findByUserIdAndAssetNameForUpdate(customerId, "TRY"))
                .thenReturn(Optional.of(tryAsset));

        Order result = orderService.cancelOrder(orderId, customerId);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());

        verify(assetRepository).save(tryAsset);
        assertEquals(BigDecimal.valueOf(3000), tryAsset.getUsableSize());

        verify(orderRepository).save(pendingOrder);
    }

    @Test
    void cancelOrder_OrderNotFound() {
        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.cancelOrder(orderId, customerId));
    }

    @Test
    void cancelOrder_OrderDoesNotBelongToCustomer() {
        // Create another user to simulate ownership mismatch
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        pendingOrder.setUser(otherUser);

        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.of(pendingOrder));

        assertThrows(IllegalArgumentException.class, () -> orderService.cancelOrder(orderId, customerId));
    }

    @Test
    void cancelOrder_OrderNotPending() {
        pendingOrder.setStatus(OrderStatus.MATCHING);
        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.of(pendingOrder));

        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(orderId, customerId));
    }

    @Test
    void cancelOrder_SellOrderRefund() {
        pendingOrder.setOrderSide(OrderSide.SELL);
        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.of(pendingOrder));

        Asset applAsset = new Asset();
        applAsset.setUsableSize(BigDecimal.valueOf(10));
        when(assetRepository.findByUserIdAndAssetNameForUpdate(customerId, "APPL"))
                .thenReturn(Optional.of(applAsset));

        Order result = orderService.cancelOrder(orderId, customerId);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());

        verify(assetRepository).save(applAsset);
        assertEquals(BigDecimal.valueOf(20), applAsset.getUsableSize());

        verify(orderRepository).save(pendingOrder);
    }
}
