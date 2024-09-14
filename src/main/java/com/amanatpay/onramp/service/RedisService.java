package com.amanatpay.onramp.service;

import com.amanatpay.onramp.dto.RateBooking;
import com.amanatpay.onramp.util.EncryptionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Service class for managing rate bookings in Redis.
 */
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final EncryptionUtil encryptionUtil;

    @Value("${default.numberOfAllowedBookingDuplicates}")
    private int numberOfAllowedBookingDuplicates;

    /**
     * Constructs a new RedisService with the specified RedisTemplate and EncryptionUtil.
     *
     * @param redisTemplate  the RedisTemplate to use for Redis operations
     * @param encryptionUtil the EncryptionUtil to use for encrypting and decrypting data
     */
    public RedisService(RedisTemplate<String, Object> redisTemplate, EncryptionUtil encryptionUtil) {
        this.redisTemplate = redisTemplate;
        this.encryptionUtil = encryptionUtil;
    }

    /**
     * Locks a rate booking in Redis with a specified duration.
     *
     * @param bookingId the unique identifier for the booking
     * @param booking   the RateBooking object containing booking details
     * @param duration  the duration for which the booking should be locked
     * @param timeUnit  the time unit for the duration (e.g., SECONDS, MINUTES)
     */
    public void lockRate(String bookingId, RateBooking booking, long duration, TimeUnit timeUnit) {
        String compositeKey = booking.getMobileNumber() + ":" + booking.getBookingId();
        String encryptedBooking = encryptionUtil.encrypt(booking.toString());
        redisTemplate.opsForValue().set(bookingId, encryptedBooking, duration, timeUnit);
        redisTemplate.opsForValue().set(compositeKey, bookingId, duration, timeUnit);
    }

    /**
     * Retrieves a rate booking from Redis.
     *
     * @param bookingId the unique identifier for the booking
     * @return the RateBooking object containing booking details
     */
    public RateBooking getRateBooking(String bookingId) {
        String encryptedBooking = (String) redisTemplate.opsForValue().get(bookingId);
        String decryptedBooking = encryptionUtil.decrypt(encryptedBooking);
        return convertStringToRateBooking(decryptedBooking);
    }

    /**
     * Expires a rate booking in Redis.
     *
     * @param bookingId the unique identifier for the booking
     */
    public void expireBooking(String bookingId) {
        redisTemplate.delete(bookingId);
    }

    /**
     * Converts a string representation of a RateBooking back to a RateBooking object.
     *
     * @param bookingString the string representation of the RateBooking
     * @return the RateBooking object
     * @throws RuntimeException if the conversion fails
     */
    private RateBooking convertStringToRateBooking(String bookingString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(bookingString, RateBooking.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert string to RateBooking", e);
        }
    }

    /**
     * Checks if a rate booking exists in Redis and is still valid (not expired).
     *
     * @param bookingId    the unique identifier for the booking (optional)
     * @param compositeKey the composite key for the booking (optional)
     * @return true if the booking exists and is valid, false otherwise
     */
    public boolean isBookingValid(String bookingId, String compositeKey) {
        boolean isBookingIdValid = bookingId != null && redisTemplate.hasKey(bookingId);
        boolean isCompositeKeyValid = compositeKey != null && redisTemplate.hasKey(compositeKey);
        return isBookingIdValid || isCompositeKeyValid;
    }

        /**
 * Checks if a rate booking exists in Redis and is still valid (not expired),
 * limiting the check to a maximum of three times.
 *
 * @param bookingId    the unique identifier for the booking (optional)
 * @param compositeKey the composite key for the booking (optional)
 * @return true if the booking exists and is valid, and the count is three or less, false otherwise
 */
public boolean isBookingValidNumberOfTimes(String bookingId, String compositeKey) {
    int validCount = 0;

    if (bookingId != null && redisTemplate.hasKey(bookingId)) {
        validCount++;
    }

    if (compositeKey != null && redisTemplate.hasKey(compositeKey)) {
        validCount++;
    }

    // Add additional checks if there are more keys to validate
    // For example, if you have more composite keys to check, add them here

    return validCount > 0 && validCount <= numberOfAllowedBookingDuplicates;
}
}