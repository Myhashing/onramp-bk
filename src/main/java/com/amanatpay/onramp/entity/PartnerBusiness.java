package com.amanatpay.onramp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
public class PartnerBusiness extends Auditable {

    @Id
    private Long id;
    private String name;
    private BigDecimal commissionValue;
    private String commissionType; // "PERCENTAGE" or "FIXED"





}