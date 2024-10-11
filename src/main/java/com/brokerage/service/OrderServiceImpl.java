package com.brokerage.service;

import com.brokerage.exception.InsufficientBalanceException;
import com.brokerage.exception.ResourceNotFoundException;
import com.brokerage.models.entity.Asset;
import com.brokerage.models.entity.Order;

import com.brokerage.repository.AssetRepository;
import com.brokerage.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;

    public OrderServiceImpl(OrderRepository orderRepository, AssetRepository assetRepository) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
    }

    @Transactional
    @Override
    public Order createOrder(UUID customerId, String assetName, String orderSide, BigDecimal size, BigDecimal price) {

        // @todo handle if assetName equals TRY
        if ("BUY".equalsIgnoreCase(orderSide)) {
            checkAndDeductBalanceForBuy(customerId, size, price);
        } else if ("SELL".equalsIgnoreCase(orderSide)) {
            checkAndDeductBalanceForSell(customerId, assetName, size);
        } else {
            throw new IllegalArgumentException("Invalid order side: " + orderSide);
        }

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setAssetName(assetName);
        order.setOrderSide(orderSide);
        order.setSize(size);
        order.setPrice(price);
        order.setStatus("PENDING");
        return orderRepository.save(order);
    }



    private void checkAndDeductBalanceForBuy(UUID customerId, BigDecimal size, BigDecimal price) {
        BigDecimal totalAmount = size.multiply(price);

        // Lock and check the TRY asset balance
        Asset tryAsset = assetRepository.findByCustomerIdAndAssetNameForUpdate(customerId, "TRY")
                .orElseThrow(() -> new ResourceNotFoundException("TRY asset not found for customer with ID: " + customerId));

        if (tryAsset.getUsableSize().compareTo(totalAmount) < 0) {
            throw new InsufficientBalanceException("Insufficient TRY balance. Required: " + totalAmount + ", Available: " + tryAsset.getUsableSize());
        }

        // Deduct the required TRY balance
        tryAsset.setUsableSize(tryAsset.getUsableSize().subtract(totalAmount));
        assetRepository.save(tryAsset);
    }

    private void checkAndDeductBalanceForSell(UUID customerId, String assetName, BigDecimal size) {
        // Lock and check the specified asset balance
        Asset assetToSell = assetRepository.findByCustomerIdAndAssetNameForUpdate(customerId, assetName)
                .orElseThrow(() -> new ResourceNotFoundException(assetName + " asset not found for customer with ID: " + customerId));

        if (assetToSell.getUsableSize().compareTo(size) < 0) {
            throw new InsufficientBalanceException("Insufficient " + assetName + " balance. Required: " + size + ", Available: " + assetToSell.getUsableSize());
        }

        // Deduct the required asset balance
        assetToSell.setUsableSize(assetToSell.getUsableSize().subtract(size));
        assetRepository.save(assetToSell);
    }

    @Transactional
    @Override
    public Order cancelOrder(UUID orderId, UUID customerId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!order.getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("Order does not belong to the specified customer.");
        }

        if (!"PENDING".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalStateException("Only PENDING orders can be canceled.");
        }

        if ("BUY".equalsIgnoreCase(order.getOrderSide())) {
            refundTryBalance(order);
        } else if ("SELL".equalsIgnoreCase(order.getOrderSide())) {
            refundAssetBalance(order);
        }

        order.setStatus("CANCELED");
        orderRepository.save(order);
        return order;
    }
    private void refundTryBalance(Order order) {
        BigDecimal refundAmount = order.getSize().multiply(order.getPrice());

        Asset tryAsset = assetRepository.findByCustomerIdAndAssetNameForUpdate(order.getCustomerId(), "TRY")
                .orElseThrow(() -> new ResourceNotFoundException("TRY asset not found for customer with ID: " + order.getCustomerId()));

        tryAsset.setUsableSize(tryAsset.getUsableSize().add(refundAmount));
        assetRepository.save(tryAsset);

        System.out.println("Refunded " + refundAmount + " TRY to customer " + order.getCustomerId());
    }

    private void refundAssetBalance(Order order) {
        Asset soldAsset = assetRepository.findByCustomerIdAndAssetNameForUpdate(order.getCustomerId(), order.getAssetName())
                .orElseThrow(() -> new ResourceNotFoundException(order.getAssetName() + " asset not found for customer with ID: " + order.getCustomerId()));

        soldAsset.setUsableSize(soldAsset.getUsableSize().add(order.getSize()));
        assetRepository.save(soldAsset);

        System.out.println("Refunded " + order.getSize() + " of " + order.getAssetName() + " to customer " + order.getCustomerId());
    }
}
