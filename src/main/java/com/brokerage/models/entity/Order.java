package com.brokerage.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderId;

    private UUID customerId;
    private String assetName;
    private String orderSide; // BUY or SELL
    private int size;
    private double price;
    private String status; // PENDING, MATCHED, CANCELED
    private LocalDateTime createDate = LocalDateTime.now();

    // Getters and setters
}


