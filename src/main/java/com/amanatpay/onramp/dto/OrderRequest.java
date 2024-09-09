package com.amanatpay.onramp.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderRequest {
    private UUID userId;
    private UUID businessId;
    private String cryptocurrency;
    private BigDecimal fiatAmount;
}