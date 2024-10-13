package com.brokerage.service;


import com.brokerage.models.dto.DepositDTO;
import com.brokerage.models.dto.WithdrawDTO;
import com.brokerage.models.entity.User;
import com.brokerage.models.request.DepositRequest;
import com.brokerage.models.request.WithdrawRequest;
import lombok.With;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransactionService {
    void deposit(UUID userId, BigDecimal amount);

    void withdraw(UUID userId, BigDecimal amount, String iban);

    UUID publishDepositEvent(DepositDTO depositDTO);

    UUID publishWithdrawEvent(WithdrawDTO withdrawDTO);
}