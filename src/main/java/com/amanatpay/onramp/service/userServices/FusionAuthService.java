package com.amanatpay.onramp.service.userServices;

import com.amanatpay.onramp.exception.FusionAuthException;
import com.amanatpay.onramp.exception.UserNotFoundException;
import com.inversoft.error.Errors;
import com.inversoft.rest.ClientResponse;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.User;
import io.fusionauth.domain.api.UserRequest;
import io.fusionauth.domain.api.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FusionAuthService {


    @Value("${fusionauth.application_id}")
    private String applicationId;

    @Value("${fusionauth.passwordless.url}")
    private String startUrl;

    @Value("${fusionauth.passwordless.login_url}")
    private String loginUrl;

    @Value("${fusionauth.search_url}")
    private String searchUrl;

    @Value("${fusionauth.bearer_token}")
    private String bearerToken;

    @Value("${fusionauth.base-url}")
    private String baseUrl;

    @Value("${fusionauth.api-key}")
    private String apiKey;

    private FusionAuthClient client;
    private static final Logger logger = LoggerFactory.getLogger(FusionAuthService.class);

    public FusionAuthService() {
        client = new FusionAuthClient(apiKey, baseUrl);
    }

    public User getUserByEmail(String email) {
        ClientResponse<UserResponse, Errors> response = client.retrieveUserByEmail(email);
        if (response.wasSuccessful()) {
            return response.successResponse.user;
        } else if (response.errorResponse != null) {
            // Error Handling
            Errors errors = response.errorResponse;
        } else if (response.exception != null) {
            // Exception Handling
            Exception exception = response.exception;
        }

        return null;
    }


    public User searchUserByUsername(String username) throws FusionAuthException, UserNotFoundException {
        client = new FusionAuthClient(apiKey, baseUrl);

        ClientResponse<UserResponse, Errors> response = client.retrieveUserByUsername(username);

        if (response.wasSuccessful()) {
            return response.successResponse.user;
        } else if (response.errorResponse != null) {
            Errors errors = response.errorResponse;

            // Check if the user was not found
            if (isUserNotFoundError(errors)) {
                logger.warn("User not found with username: {}", username);
                throw new UserNotFoundException("User not found with username: " + username);
            } else {
                // Log other errors
                logger.error("Error retrieving user by username '{}': {}", username, errors);
                throw new FusionAuthException("Failed to retrieve user by username: " + username, errors);
            }
        } else if (response.exception != null) {
            // Log the exception
            logger.error("Exception occurred while retrieving user by username '{}'", username, response.exception);
            throw new FusionAuthException("An exception occurred while retrieving user by username: " + username, response.exception);
        } else {
            // Handle unexpected cases
            logger.error("Unknown error occurred while retrieving user by username '{}'", username);
            throw new FusionAuthException("Unknown error occurred while retrieving user by username: " + username);
        }
    }

    private boolean isUserNotFoundError(Errors errors) {
        // Implement logic to determine if the error indicates that the user was not found
        // This might involve checking error codes or messages provided by FusionAuth

        // Example implementation (adjust based on actual FusionAuth error structure)
        return errors.generalErrors.stream().anyMatch(error -> "User not found".equalsIgnoreCase(error.message));
    }

    public User searchUserByMobileNumber(String mobileNumber) {
        HttpHeaders searchHeaders = new HttpHeaders();
        searchHeaders.set("Content-Type", "application/json");
        searchHeaders.set("Authorization", bearerToken);
        Map<String, Object> searchBody = new HashMap<>();
        Map<String, String> searchQuery = new HashMap<>();
        searchQuery.put("queryString", "mobilePhone:" + mobileNumber);
        searchBody.put("search", searchQuery);
        HttpEntity<Map<String, Object>> searchEntity = new HttpEntity<>(searchBody, searchHeaders);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Map> searchResponse = restTemplate.exchange(searchUrl, HttpMethod.POST, searchEntity, Map.class);
            if (searchResponse.getStatusCode().value() != 200 || searchResponse.getBody() == null || searchResponse.getBody().get("users") == null) {
                return null;
            }
            Map user = ((Map) ((List) searchResponse.getBody().get("users")).get(0));
            String userId = (String) user.get("id");
            return client.retrieveUser(UUID.fromString(userId)).successResponse.user;
        } catch (Exception e) {
            logger.error("Error occurred while searching for user by mobile number: {}", e.getMessage());
            return null;
        }

    }

    public User retrieveUserById(UUID userId) {
        ClientResponse<UserResponse, Errors> response = client.retrieveUser(userId);
        if (response.wasSuccessful()) {
            return response.successResponse.user;
        } else if (response.errorResponse != null) {
            // Error Handling
            Errors errors = response.errorResponse;
        } else if (response.exception != null) {
            // Exception Handling
            Exception exception = response.exception;
        }

        return null;
    }

    public ClientResponse<UserResponse, Errors> createUser(UserRequest request) {
                        client = new FusionAuthClient(apiKey, baseUrl);

        return client.createUser(null, request);
    }

    public boolean checkUserExists(UUID userId) {
        ClientResponse<UserResponse, Errors> response = client.retrieveUser(userId);
        return response.wasSuccessful();
    }

    public void updateUser(User user) {
        client.updateUser(user.id, new UserRequest(user));

    }

}

