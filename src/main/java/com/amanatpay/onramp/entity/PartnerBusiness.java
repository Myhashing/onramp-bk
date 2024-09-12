package com.amanatpay.onramp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class PartnerBusiness {

    @Id
    private Long id;
    private String name;
    private BigDecimal commissionValue;
    private String commissionType; // "PERCENTAGE" or "FIXED"

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCommissionValue() {
        return commissionValue;
    }

    public void setCommissionValue(BigDecimal commissionValue) {
        this.commissionValue = commissionValue;
    }

    public String getCommissionType() {
        return commissionType;
    }

    public void setCommissionType(String commissionType) {
        this.commissionType = commissionType;
    }
}