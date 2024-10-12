package com.brokerage.listener;

import com.brokerage.event.*;
import com.brokerage.exception.InsufficientBalanceException;
import com.brokerage.models.entity.Order;
import com.brokerage.service.OrderService;
import com.brokerage.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Slf4j
@Service
public class TransactionEventListener {
    private final ObjectMapper objectMapper;
    private final TransactionService transactionService;
    public TransactionEventListener(ObjectMapper objectMapper, TransactionService transactionService) {
        this.objectMapper = objectMapper;
        this.transactionService = transactionService;
    }

    @RetryableTopic(
            attempts = "3",
            exclude = {IllegalArgumentException.class},
            autoCreateTopics = "false"
    )
    @KafkaListener(topics = "deposit-topic", groupId = "brokerage-group")
    @Transactional
    public void handleDeposit(String event) {
        try {
            log.info("OrderEventListener | HandleDeposit listener started: {}", event);
            DepositEvent depositEvent = objectMapper.readValue(event, DepositEvent.class);
            transactionService.deposit(depositEvent.customerId(), depositEvent.amount());
            log.info("OrderEventListener | HandleDeposit listener finished: {}", event);
        } catch (JsonProcessingException e) {
            log.info("OrderEventListener | HandleDeposit listener failed: {}", event);
            throw new RuntimeException(e);
        }

    }

    @RetryableTopic(
            attempts = "3",
            exclude = {IllegalArgumentException.class},
            autoCreateTopics = "false"
    )
    @KafkaListener(topics = "withdraw-topic", groupId = "brokerage-group")
    @Transactional
    public void handleWithdraw(String event) {
        try {
            log.info("OrderEventListener | HandleWithdraw listener started: {}", event);
            WithdrawEvent withdrawEvent = objectMapper.readValue(event, WithdrawEvent.class);
            transactionService.withdraw(withdrawEvent.customerId(), withdrawEvent.amount(), withdrawEvent.iban());
            log.info("OrderEventListener | HandleWithdraw listener finished: {}", event);
        } catch (JsonProcessingException e) {
            log.info("OrderEventListener | HandleWithdraw listener failed: {}", event);
            throw new RuntimeException(e);
        }

    }


}
