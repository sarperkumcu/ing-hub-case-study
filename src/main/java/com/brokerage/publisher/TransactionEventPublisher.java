package com.brokerage.publisher;

import com.brokerage.event.*;
import com.brokerage.models.dto.DepositDTO;
import com.brokerage.models.dto.WithdrawDTO;
import com.brokerage.models.entity.User;
import com.brokerage.models.request.DepositRequest;
import com.brokerage.models.request.WithdrawRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public UUID publishDepositEvent(DepositDTO depositDTO) {
        UUID eventId = UUID.randomUUID();
        DepositEvent event = new DepositEvent(eventId, depositDTO.userId(), depositDTO.amount());
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

    public UUID publishWithdrawEvent(WithdrawDTO withdrawDTO){
        UUID eventId = UUID.randomUUID();
        WithdrawEvent event = new WithdrawEvent(eventId, withdrawDTO.userId(), withdrawDTO.amount(), withdrawDTO.iban());
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
