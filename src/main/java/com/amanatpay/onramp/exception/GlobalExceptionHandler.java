package com.amanatpay.onramp.exception;

import com.amanatpay.onramp.dto.ErrorResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        Map<String, List<ErrorResponse.FieldError>> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(fieldError -> {
                            ErrorResponse.FieldError error = new ErrorResponse.FieldError();
                            error.setCode(fieldError.getCode());
                            error.setMessage(fieldError.getDefaultMessage());
                            return error;
                        }, Collectors.toList())
                ));

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage("Validation Error");
        errorResponse.setErrors(errors);
        errorResponse.setFieldErrors(fieldErrors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException ex) {
        String responseBody = ex.getResponseBodyAsString();
        Map<String, List<ErrorResponse.FieldError>> fieldErrors = parseFieldErrors(responseBody);
        List<String> generalErrors = parseGeneralErrors(responseBody);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(ex.getStatusCode().value());
        errorResponse.setMessage("FusionAuth Error");
        errorResponse.setErrors(generalErrors);
        errorResponse.setFieldErrors(fieldErrors);

        return new ResponseEntity<>(errorResponse, ex.getStatusCode());
    }

    private Map<String, List<ErrorResponse.FieldError>> parseFieldErrors(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<ErrorResponse.FieldError>> fieldErrors = new HashMap<>();

        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode fieldErrorsNode = rootNode.path("fieldErrors");

            fieldErrorsNode.fields().forEachRemaining(entry -> {
                String fieldName = entry.getKey();
                List<ErrorResponse.FieldError> errors = new ArrayList<>();
                entry.getValue().forEach(errorNode -> {
                    ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError();
                    fieldError.setCode(errorNode.path("code").asText());
                    fieldError.setMessage(errorNode.path("message").asText());
                    errors.add(fieldError);
                });
                fieldErrors.put(fieldName, errors);
            });
        } catch (Exception e) {
            // Log the exception
        }

        return fieldErrors;
    }

    private List<String> parseGeneralErrors(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> generalErrors = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode generalErrorsNode = rootNode.path("generalErrors");

            generalErrorsNode.forEach(errorNode -> {
                generalErrors.add(errorNode.asText());
            });
        } catch (Exception e) {
            // Log the exception
        }

        return generalErrors;
    }
}