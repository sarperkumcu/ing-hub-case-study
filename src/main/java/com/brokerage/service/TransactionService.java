package com.brokerage.service;


import com.brokerage.models.request.DepositRequest;
import com.brokerage.models.request.WithdrawRequest;
import lombok.With;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionService {
    void deposit(UUID customerId, BigDecimal amount);

    void withdraw(UUID customer, BigDecimal amount, String iban);

    UUID publishDepositEvent(DepositRequest depositRequest);

    UUID publishWithdrawEvent(WithdrawRequest withdrawRequest);
}