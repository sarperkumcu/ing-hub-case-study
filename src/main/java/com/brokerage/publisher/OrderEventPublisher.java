package com.brokerage.publisher;

import com.brokerage.event.*;
import com.brokerage.models.dto.CancelOrderDTO;
import com.brokerage.models.dto.CreateOrderDTO;
import com.brokerage.models.entity.User;
import com.brokerage.models.request.CancelOrderRequest;
import com.brokerage.models.request.CreateOrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@Service
public class OrderEventPublisher {
    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventPublisher(ObjectMapper objectMapper, KafkaTemplate<String, Object> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public UUID publishCreateOrderEvent(CreateOrderDTO createOrderDTO) {
        UUID eventId = UUID.randomUUID();
        CreateOrderEvent event = new CreateOrderEvent(eventId, createOrderDTO.userId(), createOrderDTO.assetName(),
                createOrderDTO.orderSide(), createOrderDTO.size(), createOrderDTO.price());
        String message = null;
        try {
            message = objectMapper.writeValueAsString(event);
            log.info("OrderEventPublisher | CreateOrderEvent publish started: {}", message);
            kafkaTemplate.send("create-order-topic", message);
            log.info("OrderEventPublisher | CreateOrderEvent publish finished: {}", message);
        } catch (JsonProcessingException e) {
            log.info("OrderEventPublisher | CreateOrderEvent publish failed: {}", message);
            throw new RuntimeException(e);
        }
        return eventId;
    }

    public UUID publishCancelOrderEvent(CancelOrderDTO cancelOrderDTO){
        UUID eventId = UUID.randomUUID();
        CancelOrderEvent event = new CancelOrderEvent(eventId, cancelOrderDTO.orderId(), cancelOrderDTO.userId());
        String message = null;
        try {
            message = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        kafkaTemplate.send("cancel-order-topic", message);
        return eventId;

    }

}
