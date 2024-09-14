package com.amanatpay.onramp.controller;

import com.amanatpay.onramp.dto.ApiResponse;
import com.amanatpay.onramp.dto.FinalRate;
import com.amanatpay.onramp.dto.RateBooking;
import com.amanatpay.onramp.service.PriceCalculationService;
import com.amanatpay.onramp.service.RateBookingService;
import com.amanatpay.onramp.service.RedisService;
import com.amanatpay.onramp.service.SpreadService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/rate")
public class RateController {

    private final PriceCalculationService priceCalculationService;

    private final SpreadService spreadService;

    private final RedisService redisService;

    private final RateBookingService rateBookingService;


    @Value("${default.systemFee}")
    private BigDecimal defaultSystemFee;

    @Value("${default.transactionFee}")
    private double defaultTransactionFee;

    @Value("${default.redirectBaseUrl}")
    private String redirectBaseUrl;

    public RateController(PriceCalculationService priceCalculationService, SpreadService spreadService, RedisService redisService, RateBookingService rateBookingService) {
        this.priceCalculationService = priceCalculationService;
        this.spreadService = spreadService;
        this.redisService = redisService;
        this.rateBookingService = rateBookingService;
    }


    @GetMapping
    public ApiResponse<Map<String, BigDecimal>> getRate(@RequestParam @NotNull Long businessId) {
        try {
            BigDecimal wap = priceCalculationService.calculateWAP();
            BigDecimal rateWithSpread = spreadService.applyDynamicSpread(wap);
            BigDecimal finalRate = priceCalculationService.applyCommission(rateWithSpread, businessId);
            return new ApiResponse<>(200, "OK", Map.of("rate", finalRate), null);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Failed to retrieve rate: " + e.getMessage(), null, e.getMessage());
        }
    }

    @GetMapping("/final")
    public ApiResponse<FinalRate> getRateFinal(@RequestParam @NotNull Long businessId, @RequestParam @NotNull BigDecimal amount) {
        try {
            FinalRate finalRate = priceCalculationService.calculateFinalRate(businessId, amount, defaultSystemFee, defaultTransactionFee);
            return new ApiResponse<>(200, "OK", finalRate, null);
        } catch (Exception e) {
            return new ApiResponse<>(500, "Failed to retrieve rate: " + e.getMessage(), null, e.getMessage());
        }
    }

    /**
     * Books a rate for a given business and user.
     *
     * @param businessId     The ID of the business.
     * @param amount         The amount for which the rate is being booked.
     * @param partnerUserId  The ID of the partner user.
     * @param mobileNumber   The mobile number of the user.
     * @param nationalCode   (Optional) The national code of the user.
     * @param postcode       (Optional) The postcode of the user.
     * @param birthdate      (Optional) The birthdate of the user.
     * @param email          (Optional) The email address of the user.
     * @param bankCardNumber (Optional) The bank card number of the user.
     * @param iban           (Optional) The IBAN of the user.
     * @param kycImage       (Optional) The KYC image of the user.
     * @return ApiResponse containing the booking details or an error message.
     */
    @PostMapping("/book")
    public ApiResponse<Map<String, Object>> bookRate(@RequestParam @NotNull Long businessId, @RequestParam @NotNull BigDecimal amount, @RequestParam @NotNull Long partnerUserId, @RequestParam @NotNull String mobileNumber, @RequestParam(required = false) String nationalCode, @RequestParam(required = false) String postcode, @RequestParam(required = false) String birthdate, @RequestParam(required = false) String email, @RequestParam(required = false) String bankCardNumber, @RequestParam(required = false) String iban, @RequestParam(required = false) MultipartFile kycImage) {
        try {
            // Calculate the weighted average price (WAP) based on the amount
            BigDecimal wap = priceCalculationService.calculateWAPByAmount(amount);

            // Apply dynamic spread to the WAP
            BigDecimal rateWithSpread = spreadService.applyDynamicSpread(wap);

            // Apply commission to the rate with spread
            BigDecimal finalRate = priceCalculationService.applyCommission(rateWithSpread, businessId);

            // Generate a unique booking ID
            String bookingId = UUID.randomUUID().toString();

            // Create a new RateBooking object
            RateBooking booking = new RateBooking(bookingId, partnerUserId, finalRate, amount, LocalDateTime.now().plusMinutes(5));

            // Lock the rate in Redis for 5 minutes
            redisService.lockRate(bookingId, booking, 5, TimeUnit.MINUTES);

            // Save temporary user data
            rateBookingService.saveTemporaryUserData(partnerUserId, mobileNumber, nationalCode, postcode, birthdate, email, bankCardNumber, iban, kycImage);

            // Prepare the response data
            Map<String, Object> response = new HashMap<>();
            response.put("bookingId", bookingId);
            response.put("rate", finalRate);
            response.put("expiresAt", booking.getExpirationTime());
            response.put("url", redirectBaseUrl + bookingId);

            // Return a successful response
            return new ApiResponse<>(200, "Rate booking successful", response, null);
        } catch (Exception e) {
            // Return an error response in case of an exception
            return new ApiResponse<>(500, "Failed to book rate: " + e.getMessage(), null, e.getMessage());
        }
    }
}


