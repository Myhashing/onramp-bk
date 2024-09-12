package com.amanatpay.onramp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "`order`")
@Data
public class Order extends Auditable {

    @Id
    @Column(name = "order_id", columnDefinition = "BINARY(16)")
    private UUID orderId;

    @Column(name = "business_id", columnDefinition = "BINARY(16)")
    private UUID businessId;



    @Column(name = "crypto_amount", nullable = false, precision = 38, scale = 2)
    private BigDecimal cryptoAmount;

    @Column(name = "cryptocurrency", nullable = false)
    private String cryptocurrency;

    @Column(name = "exchange_rate", nullable = false, precision = 38, scale = 2)
    private BigDecimal exchangeRate;

    @Column(name = "fees", nullable = false, precision = 38, scale = 2)
    private BigDecimal fees;

    @Column(name = "fiat_amount", nullable = false, precision = 38, scale = 2)
    private BigDecimal fiatAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;



    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID userId;

    // Getters and setters
}