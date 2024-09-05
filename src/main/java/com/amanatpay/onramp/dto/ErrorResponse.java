package com.amanatpay.onramp.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ErrorResponse {
    private int status;
    private String message;
    private List<String> errors;
    private Map<String, List<FieldError>> fieldErrors = null;

    public static class FieldError {
        private String code;
        private String message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}