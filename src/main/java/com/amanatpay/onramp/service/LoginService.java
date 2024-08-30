package com.amanatpay.onramp.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class LoginService {

    public Map<String, Object> login(String username, String password) {
        String url = "http://localhost:9011/oauth2/token";

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        // Create body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", "f7427948-34ae-4dc0-920d-f50316d67d70");
        body.add("client_secret", "RLN9NlryIkmXw6_BNbsxs-95sOlHpRV7nG11M0jgonI");
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);
        body.add("scope", "openid profile email");

        // Create entity
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        // Create RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Send request
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        // Return the full response body
        return response.getBody();
    }
}