package com.amanatpay.onramp.entity;

import jakarta.persistence.*;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
public class TradeData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private long time;

    private BigDecimal price;

    private BigDecimal volume;

    private String type;

    private String currency;

    private Timestamp timestamp;
}