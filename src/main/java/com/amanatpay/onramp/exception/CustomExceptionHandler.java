package com.amanatpay.onramp.exception;

import com.amanatpay.onramp.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse<List<String>> handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        List<String> errors = List.of("Missing required parameter: " + name);
        return new ApiResponse<>(400, "Missing required parameters", errors, "Missing required parameters");
    }
}