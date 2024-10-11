package com.brokerage.publisher;

import com.brokerage.event.*;
import com.brokerage.models.request.CancelOrderRequest;
import com.brokerage.models.request.CreateOrderRequest;
import com.brokerage.models.request.DepositRequest;
import com.brokerage.models.request.WithdrawRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.With;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Service
public class TransactionEventPublisher {
    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TransactionEventPublisher(ObjectMapper objectMapper, KafkaTemplate<String, Object> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public UUID publishDepositEvent(DepositRequest depositRequest) {
        UUID eventId = UUID.randomUUID();
        DepositEvent event = new DepositEvent(eventId, depositRequest.getCustomerId(), depositRequest.getAmount());
        String message = null;
        try {
            message = objectMapper.writeValueAsString(event);
            log.info("TransactionEventPublisher | DepositEvent publish started: {}", message);
            kafkaTemplate.send("deposit-topic", message);
            log.info("TransactionEventPublisher | DepositEvent publish finished: {}", message);
        } catch (JsonProcessingException e) {
            log.info("TransactionEventPublisher | DepositEvent publish failed: {}", message);
            throw new RuntimeException(e);
        }
        return eventId;
    }

    public UUID publishWithdrawEvent(WithdrawRequest withdrawRequest){
        UUID eventId = UUID.randomUUID();
        WithdrawEvent event = new WithdrawEvent(eventId, withdrawRequest.getCustomerId(), withdrawRequest.getAmount(), withdrawRequest.getIban());
        String message = null;
        try {
            message = objectMapper.writeValueAsString(event);
            log.info("TransactionEventPublisher | WithdrawEvent publish started: {}", message);
            kafkaTemplate.send("withdraw-topic", message);
            log.info("TransactionEventPublisher | WithdrawEvent publish finished: {}", message);
        } catch (JsonProcessingException e) {
            log.info("TransactionEventPublisher | WithdrawEvent publish failed: {}", message);
            throw new RuntimeException(e);
        }
        return eventId;

    }

}
