package com.amanatpay.onramp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FinalRate {


    private BigDecimal rate;
    private BigDecimal systemFee;
    private BigDecimal transactionFee;
    private BigDecimal totalAmount;
    private BigDecimal usdtAmount;

}
