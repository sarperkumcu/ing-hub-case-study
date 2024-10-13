package com.brokerage.service;

import com.brokerage.models.dto.CancelOrderDTO;
import com.brokerage.models.dto.CreateOrderDTO;
import com.brokerage.models.entity.User;
import com.brokerage.models.enums.OrderSide;
import com.brokerage.models.enums.OrderStatus;
import com.brokerage.service.interfaces.OrderService;
import com.brokerage.specification.OrderSpecification;
import com.brokerage.exception.InsufficientBalanceException;
import com.brokerage.exception.ResourceNotFoundException;
import com.brokerage.models.entity.Asset;
import com.brokerage.models.entity.Order;

import com.brokerage.publisher.OrderEventPublisher;
import com.brokerage.repository.AssetRepository;
import com.brokerage.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final AssetRepository assetRepository;
    private final OrderEventPublisher eventProducer;

    private final UserDetailsServiceImpl userDetailsService;


    public OrderServiceImpl(OrderRepository orderRepository, AssetRepository assetRepository, OrderEventPublisher eventProducer, UserDetailsServiceImpl userDetailsService) {
        this.orderRepository = orderRepository;
        this.assetRepository = assetRepository;
        this.eventProducer = eventProducer;
        this.userDetailsService = userDetailsService;
    }


    public UUID publishCreateOrderEvent(CreateOrderDTO createOrderDTO) {
        return eventProducer.publishCreateOrderEvent(createOrderDTO);
    }

    public UUID publishCancelOrderEvent(CancelOrderDTO cancelOrderDTO) {
        return eventProducer.publishCancelOrderEvent(cancelOrderDTO);
    }

    @Override
    public Page<Order> getOrders(UUID customerId, LocalDateTime startDate, LocalDateTime endDate, String assetName, String orderSide, String status, Pageable pageable) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getDetails();

        Specification<Order> spec = Specification.where(OrderSpecification.userIdEquals(user.getId()));

        if (startDate != null && endDate != null) {
            spec = spec.and(OrderSpecification.createDateBetween(startDate, endDate));
        }
        if (assetName != null && !assetName.isEmpty()) {
            spec = spec.and(OrderSpecification.assetNameEquals(assetName));
        }
        if (orderSide != null && !orderSide.isEmpty()) {
            spec = spec.and(OrderSpecification.orderSideEquals(orderSide));
        }
        if (status != null && !status.isEmpty()) {
            spec = spec.and(OrderSpecification.statusEquals(status));
        }

        return orderRepository.findAll(spec, pageable);
    }

    @Transactional
    @Override
    public Order createOrder(UUID userId, String assetName, String orderSideString, BigDecimal size, BigDecimal price) {

        OrderSide orderSide;
        try {
            orderSide = OrderSide.valueOf(orderSideString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order side: " + orderSideString);
        }
        // @todo handle if assetName equals TRY
        // @todo map orderside to enum
        if (OrderSide.BUY.equals(orderSide)) {
            checkAndDeductBalanceForBuy(userId, size, price);
        } else if (OrderSide.SELL.equals(orderSide)) {
            checkAndDeductBalanceForSell(userId, assetName, size);
        } else {
            throw new IllegalArgumentException("Invalid order side: " + orderSide);
        }

        User user = userDetailsService.getUserById(userId);

        Order order = new Order();
        order.setUser(user);
        order.setAssetName(assetName);
        order.setOrderSide(orderSide);
        order.setSize(size);
        order.setPrice(price);
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    private void checkAndDeductBalanceForBuy(UUID customerId, BigDecimal size, BigDecimal price) {
        BigDecimal totalAmount = size.multiply(price);

        Asset tryAsset = assetRepository.findByUserIdAndAssetNameForUpdate(customerId, "TRY")
                .orElseThrow(() -> new ResourceNotFoundException("TRY asset not found for customer with ID: " + customerId));

        if (tryAsset.getUsableSize().compareTo(totalAmount) < 0) {
            throw new InsufficientBalanceException("Insufficient TRY balance. Required: " + totalAmount + ", Available: " + tryAsset.getUsableSize());
        }

        tryAsset.setUsableSize(tryAsset.getUsableSize().subtract(totalAmount));
        assetRepository.save(tryAsset);
    }

    private void checkAndDeductBalanceForSell(UUID customerId, String assetName, BigDecimal size) {
        Asset assetToSell = assetRepository.findByUserIdAndAssetNameForUpdate(customerId, assetName)
                .orElseThrow(() -> new ResourceNotFoundException(assetName + " asset not found for customer with ID: " + customerId));

        if (assetToSell.getUsableSize().compareTo(size) < 0) {
            throw new InsufficientBalanceException("Insufficient " + assetName + " balance. Required: " + size + ", Available: " + assetToSell.getUsableSize());
        }

        assetToSell.setUsableSize(assetToSell.getUsableSize().subtract(size));
        assetRepository.save(assetToSell);
    }

    @Transactional
    @Override
    public Order cancelOrder(UUID orderId, UUID customerId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!order.getUser().getId().equals(customerId)) {
            throw new IllegalArgumentException("Order does not belong to the specified customer.");
        }

        if (!OrderStatus.PENDING.name().equalsIgnoreCase(order.getStatus().name())) {
            throw new IllegalStateException("Only PENDING orders can be canceled.");
        }

        if (OrderSide.BUY.equals(order.getOrderSide())) {
            refundTryBalance(order);
        } else if (OrderSide.SELL.equals(order.getOrderSide())) {
            refundAssetBalance(order);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return order;
    }
    private void refundTryBalance(Order order) {
        BigDecimal refundAmount = order.getSize().multiply(order.getPrice());

        Asset tryAsset = assetRepository.findByUserIdAndAssetNameForUpdate(order.getUser().getId(), "TRY")
                .orElseThrow(() -> new ResourceNotFoundException("TRY asset not found for customer with ID: " + order.getUser().getId()));

        tryAsset.setUsableSize(tryAsset.getUsableSize().add(refundAmount));
        assetRepository.save(tryAsset);

        System.out.println("Refunded " + refundAmount + " TRY to customer " + order.getUser().getId());
    }

    private void refundAssetBalance(Order order) {
        Asset soldAsset = assetRepository.findByUserIdAndAssetNameForUpdate(order.getUser().getId(), order.getAssetName())
                .orElseThrow(() -> new ResourceNotFoundException(order.getAssetName() + " asset not found for customer with ID: " + order.getUser().getId()));

        soldAsset.setUsableSize(soldAsset.getUsableSize().add(order.getSize()));
        assetRepository.save(soldAsset);

        System.out.println("Refunded " + order.getSize() + " of " + order.getAssetName() + " to customer " + order.getUser().getId());
    }

    @Transactional
    @Override
    public Order matchPendingOrder(UUID orderId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!OrderStatus.PENDING.equals(order.getStatus())) {
            throw new IllegalStateException("Only PENDING orders can be matched.");
        }

        if (OrderSide.BUY.equals(order.getOrderSide())) {
            matchBuyOrder(order);
        } else if (OrderSide.SELL.equals(order.getOrderSide())) {
            matchSellOrder(order);
        }

        order.setStatus(OrderStatus.MATCHING);
        return orderRepository.save(order);
    }

    private void matchBuyOrder(Order order) {
        UUID userId = order.getUser().getId();
        User user = userDetailsService.getUserById(userId);
        BigDecimal totalSize = order.getSize();

        Asset boughtAsset = assetRepository.findByUserIdAndAssetNameForUpdate(userId, order.getAssetName())
                .orElseGet(() -> createNewAsset(user, order.getAssetName()));
        Asset tryAsset = assetRepository.findByUserIdAndAssetNameForUpdate(userId, "TRY")
                .orElseThrow(() -> new ResourceNotFoundException(order.getAssetName() + " asset not found for user ID: " + userId));

        boughtAsset.setSize(boughtAsset.getSize().add(totalSize));
        boughtAsset.setUsableSize(boughtAsset.getSize().add(totalSize));

        tryAsset.setSize(tryAsset.getSize().subtract(totalSize.multiply(order.getPrice())));
        assetRepository.save(boughtAsset);
        assetRepository.save(tryAsset);
    }

    private void matchSellOrder(Order order) {
        UUID userId = order.getUser().getId();
        User user = userDetailsService.getUserById(userId);
        BigDecimal totalPrice = order.getSize().multiply(order.getPrice());

        Asset soldAsset = assetRepository.findByUserIdAndAssetNameForUpdate(userId, order.getAssetName())
                .orElseThrow(() -> new ResourceNotFoundException(order.getAssetName() + " asset not found for user ID: " + userId));

        soldAsset.setSize(soldAsset.getSize().subtract(order.getSize()));
        assetRepository.save(soldAsset);

        Asset tryAsset = assetRepository.findByUserIdAndAssetNameForUpdate(userId, "TRY")
                .orElseGet(() -> createNewAsset(user, "TRY"));

        tryAsset.setSize(tryAsset.getSize().add(totalPrice));
        tryAsset.setUsableSize(tryAsset.getSize().add(totalPrice));
        assetRepository.save(tryAsset);
    }

    private Asset createNewAsset(User user, String assetName) {
        Asset newAsset = new Asset();
        newAsset.setUser(user);
        newAsset.setAssetName(assetName);
        newAsset.setSize(BigDecimal.ZERO);
        newAsset.setUsableSize(BigDecimal.ZERO);
        return newAsset;
    }
}
