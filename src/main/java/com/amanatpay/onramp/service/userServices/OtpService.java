package com.amanatpay.onramp.service.userServices;

import com.amanatpay.onramp.dto.ApiResponse;
import com.amanatpay.onramp.util.EncryptionUtil;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    private final Map<String, Map<String, String>> otpStore = new ConcurrentHashMap<>();
    private final Map<String, Bucket> rateLimiters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final EncryptionUtil encryptionUtil;

    public OtpService(EncryptionUtil encryptionUtil) {
        this.encryptionUtil = encryptionUtil;
    }

    public ApiResponse<Map<String, Object>> sendOtp(String mobileNumber, String ipAddress, String userAgent) {

        Bucket bucket = rateLimiters.computeIfAbsent(mobileNumber, k -> Bucket4j.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))))
                .build());

        if (bucket.tryConsume(1)) {

            String url = "https://console.melipayamak.com/api/send/otp/d317efb1acf147ad943589d517ae4c22";

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            // Create body
            Map<String, String> body = Map.of("to", mobileNumber);

            // Create entity
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            // Create RestTemplate instance
            RestTemplate restTemplate = new RestTemplate();

            try {
                // Send request
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
                String otpCode = (String) response.getBody().get("code");

                // Encrypt and store OTP, IP Address, and User-Agent
                Map<String, String> encryptedData = Map.of(
                        "otp", encryptionUtil.encrypt(otpCode),
                        "ipAddress", encryptionUtil.encrypt(ipAddress),
                        "userAgent", encryptionUtil.encrypt(userAgent)
                );
                otpStore.put(mobileNumber, encryptedData);

                // Schedule OTP removal after 5 minutes
                scheduler.schedule(() -> otpStore.remove(mobileNumber), 5, TimeUnit.MINUTES);
                //For testing purposes, print the OTP code in console
                System.out.println("OTP code: " + otpCode);
                // Return success response without OTP code
                return new ApiResponse<>(response.getStatusCode().value(), "OTP sent successfully", null, null);
            } catch (Exception e) {
                // Handle errors
                return new ApiResponse<>(500, "Internal Server Error", null, e.getMessage());
            }
        } else {
            return new ApiResponse<>(429, "Too Many Requests", null, "Rate limit exceeded");
        }
    }

    public boolean verifyOtp(String mobileNumber, String otpCode, String ipAddress, String userAgent) {
        Map<String, String> encryptedData = otpStore.get(mobileNumber);
        if (encryptedData != null) {
            String storedOtp = encryptionUtil.decrypt(encryptedData.get("otp"));
            String storedIpAddress = encryptionUtil.decrypt(encryptedData.get("ipAddress"));
            String storedUserAgent = encryptionUtil.decrypt(encryptedData.get("userAgent"));
            return storedOtp.equals(otpCode) && storedIpAddress.equals(ipAddress) && storedUserAgent.equals(userAgent);
        }
        return false;
    }
}