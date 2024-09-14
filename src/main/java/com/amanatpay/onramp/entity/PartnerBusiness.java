package com.amanatpay.onramp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class PartnerBusiness extends Auditable {

    @Id
    private Long id;
    private String name;
    private BigDecimal commissionValue;
    private String commissionType; // "PERCENTAGE" or "FIXED"
    private String language;


}