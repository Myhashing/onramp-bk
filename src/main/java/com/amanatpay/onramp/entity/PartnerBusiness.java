package com.amanatpay.onramp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class PartnerBusiness extends Auditable {

    @Id
    private Long id;
    private String name;
    private BigDecimal commissionValue;
    private String commissionType; // "PERCENTAGE" or "FIXED"


}