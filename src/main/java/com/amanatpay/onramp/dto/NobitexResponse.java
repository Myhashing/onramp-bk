package com.amanatpay.onramp.dto;

import lombok.Data;

import java.util.List;

@Data
public class NobitexResponse {
    private String status;
    private List<Trade> trades;

    @Data
    public static class Trade {
        private long time;
        private String price;
        private String volume;
        private String type;
    }
}