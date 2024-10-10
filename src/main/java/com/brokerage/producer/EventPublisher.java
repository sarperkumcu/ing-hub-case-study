package com.brokerage.producer;

import com.brokerage.event.*;
import com.brokerage.models.entity.Order;
import com.brokerage.models.request.CreateOrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class EventPublisher {
    private final ObjectMapper objectMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventPublisher(ObjectMapper objectMapper, KafkaTemplate<String, Object> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public UUID publishCreateOrderEvent(CreateOrderRequest createOrderRequest) {
        UUID eventId = UUID.randomUUID();
        CreateOrderEvent event = new CreateOrderEvent(eventId, createOrderRequest.getCustomerId(), createOrderRequest.getAssetName(),
                createOrderRequest.getOrderSide(), createOrderRequest.getSize(), createOrderRequest.getPrice());
        String message = null;
        try {
            message = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        kafkaTemplate.send("create-order-topic", message);
        return eventId;
    }

}
