package com.amanatpay.onramp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
public class RateBooking extends Auditable {

    @Id
    private String rateBookingId;

    @NotNull(message = "Rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be greater than 0")
    private BigDecimal rate;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Business ID is required")
    private String businessId;


}