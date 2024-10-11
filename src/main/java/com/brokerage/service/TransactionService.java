package com.brokerage.service;


import com.brokerage.models.request.DepositRequest;
import com.brokerage.models.request.WithdrawRequest;
import lombok.With;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionService {
    void deposit(UUID customerId, String assetName, String orderSide, BigDecimal size, BigDecimal price);

    void withdraw(UUID orderId, UUID customerId);

    UUID publishDepositEvent(DepositRequest depositRequest);

    UUID publishWithdrawEvent(WithdrawRequest withdrawRequest);
}