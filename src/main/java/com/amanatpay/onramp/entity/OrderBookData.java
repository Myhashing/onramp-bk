package com.amanatpay.onramp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
public class OrderBookData {

    @Id
    private UUID id;
    private BigDecimal wap;
    private BigDecimal spread;
    private Timestamp timestamp;
}