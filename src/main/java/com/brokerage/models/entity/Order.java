package com.brokerage.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "order_id", updatable = false, nullable = false)
    private UUID orderId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "asset_name", nullable = false)
    private String assetName;

    @Column(name = "order_side", nullable = false)
    private String orderSide; // Should be "BUY" or "SELL"

    @Column(name = "size", nullable = false)
    private int size;

    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "status", nullable = false)
    private String status; // Should be "PENDING", "MATCHED", or "CANCELED"

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate = LocalDateTime.now();
}
