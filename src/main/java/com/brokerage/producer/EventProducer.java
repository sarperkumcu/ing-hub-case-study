package com.brokerage.producer;

import com.brokerage.event.*;
import com.brokerage.models.entity.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCreateOrderEvent(Order order) {
        CreateOrderEvent event = new CreateOrderEvent(order.getOrderId(), order.getCustomerId(), order.getAssetName(),
                order.getOrderSide(), order.getSize(), order.getPrice());
        kafkaTemplate.send("create-order-topic", event);
    }

}
