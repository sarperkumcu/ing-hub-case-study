package com.brokerage.service;

import com.brokerage.models.request.CancelOrderRequest;
import com.brokerage.models.request.CreateOrderRequest;
import com.brokerage.models.request.DepositRequest;
import com.brokerage.models.request.WithdrawRequest;
import com.brokerage.publisher.OrderEventPublisher;
import com.brokerage.publisher.TransactionEventPublisher;
import com.brokerage.repository.AssetRepository;
import com.brokerage.repository.OrderRepository;
import lombok.With;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;


@Service
public class TransactionServiceImpl implements TransactionService{
    private final TransactionEventPublisher eventPublisher;


    public TransactionServiceImpl(TransactionEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    public UUID publishDepositEvent(DepositRequest depositRequest) {
        return eventPublisher.publishDepositEvent(depositRequest);
    }

    public UUID publishWithdrawEvent(WithdrawRequest withdrawRequest) {
        return eventPublisher.publishWithdrawEvent(withdrawRequest);
    }
    @Override
    public void deposit(UUID customerId, String assetName, String orderSide, BigDecimal size, BigDecimal price) {

    }

    @Override
    public void withdraw(UUID orderId, UUID customerId) {

    }
}
