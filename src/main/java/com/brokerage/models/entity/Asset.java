package com.brokerage.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "asset")
public class Asset {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "asset_name", nullable = false)
    private String assetName;

    @Column(name = "size", nullable = false, precision = 15, scale = 2)
    private BigDecimal size;

    @Column(name = "usable_size", nullable = false, precision = 15, scale = 2)
    private BigDecimal usableSize;
}
