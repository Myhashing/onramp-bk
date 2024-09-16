package com.amanatpay.onramp.dto;

import java.util.List;

public class PaymentOptionsResponse {
    private List<String> paymentMethods;
    private String message;

    public PaymentOptionsResponse(List<String> paymentMethods) {
        this.paymentMethods = paymentMethods;
        this.message = "Success";
    }

    public PaymentOptionsResponse(List<String> paymentMethods, String message) {
        this.paymentMethods = paymentMethods;
        this.message = message;
    }

    public List<String> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<String> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}