package com.amanatpay.onramp.dto;

public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    private String error;

    public ApiResponse(int status, String message, T data, String error) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    // Method to create error response
    public static <T> ApiResponse<T> createErrorResponse(int status, String message, String error) {
        return new ApiResponse<>(status, message, null, error);
    }

    // Getters and setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}