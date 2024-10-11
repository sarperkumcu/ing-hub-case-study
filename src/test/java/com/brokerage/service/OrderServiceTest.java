package com.brokerage.service;

import com.brokerage.exception.InsufficientBalanceException;
import com.brokerage.exception.ResourceNotFoundException;
import com.brokerage.models.entity.Asset;
import com.brokerage.models.entity.Order;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private OrderServiceImpl orderService;  // Inject the implementation class, not the interface.


    private UUID orderId;
    private UUID customerId;
    private Order pendingOrder;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        // Create a sample pending order for testing.
        pendingOrder = new Order();
        pendingOrder.setOrderId(orderId);
        pendingOrder.setCustomerId(customerId);
        pendingOrder.setAssetName("AAPL");
        pendingOrder.setOrderSide("BUY");
        pendingOrder.setSize(BigDecimal.valueOf(10));
        pendingOrder.setPrice(BigDecimal.valueOf(100));
        pendingOrder.setStatus("PENDING");
    }

    @Test
    void cancelOrder_SuccessfulCancellation() {
        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.of(pendingOrder));

        Asset tryAsset = new Asset();
        tryAsset.setUsableSize(BigDecimal.valueOf(2000));
        when(assetRepository.findByCustomerIdAndAssetNameForUpdate(customerId, "TRY"))
                .thenReturn(Optional.of(tryAsset));

        Order result = orderService.cancelOrder(orderId, customerId);

        assertEquals("CANCELED", result.getStatus());

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
        UUID otherCustomerId = UUID.randomUUID();
        pendingOrder.setCustomerId(otherCustomerId);
        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.of(pendingOrder));

        assertThrows(IllegalArgumentException.class, () -> orderService.cancelOrder(orderId, customerId));
    }

    @Test
    void cancelOrder_OrderNotPending() {
        pendingOrder.setStatus("MATCHED");
        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.of(pendingOrder));

        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(orderId, customerId));
    }

    @Test
    void cancelOrder_SellOrderRefund() {
        pendingOrder.setOrderSide("SELL");
        when(orderRepository.findByIdForUpdate(orderId)).thenReturn(Optional.of(pendingOrder));

        Asset aaplAsset = new Asset();
        aaplAsset.setUsableSize(BigDecimal.valueOf(10));
        when(assetRepository.findByCustomerIdAndAssetNameForUpdate(customerId, "AAPL"))
                .thenReturn(Optional.of(aaplAsset));

        Order result = orderService.cancelOrder(orderId, customerId);

        assertEquals("CANCELED", result.getStatus());

        verify(assetRepository).save(aaplAsset);
        assertEquals(BigDecimal.valueOf(20), aaplAsset.getUsableSize());

        verify(orderRepository).save(pendingOrder);
    }
}
