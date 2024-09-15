package com.amanatpay.onramp.service;

import com.amanatpay.onramp.dto.RateBooking;
import com.amanatpay.onramp.entity.TemporaryUserData;
import com.amanatpay.onramp.exception.BookingExpiredException;
import com.amanatpay.onramp.repository.TemporaryUserDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class RateBookingService {

    private final RedisService redisService;

    private final TemporaryUserDataRepository temporaryUserDataRepository;
    private final KycService kycService;

    public RateBookingService(RedisService redisService, TemporaryUserDataRepository temporaryUserDataRepository, KycService kycService) {
        this.redisService = redisService;
        this.temporaryUserDataRepository = temporaryUserDataRepository;
        this.kycService = kycService;
    }

    public void expireBookingIfNeeded(String bookingId) {
        RateBooking booking = redisService.getRateBooking(bookingId);

        if (booking != null && booking.getExpirationTime().isBefore(LocalDateTime.now())) {
            redisService.expireBooking(bookingId);  // Expire the booking in Redis
            throw new BookingExpiredException("The booking has expired. Please request a new rate.");
        }
    }

    //TODO: Implement the logic to check and notify expired bookings

/*    public void checkAndNotifyExpiredBookings(  ) {
    List<RateBooking> expiredBookings = bookingRepository.findExpiredBookings();  // Assuming bookings are logged

    if (!expiredBookings.isEmpty()) {
        String message = expiredBookings.size() + " rate bookings have expired.";
        notificationService.sendAlertToAdmin(message, NotificationType.RATE_BOOKING_EXPIRY);
    }*/


    public void saveTemporaryUserData(Long PartnerUserId, String mobileNumber, String nationalCode,
                                      String postcode, String birthdate, String email, String bankCardNumber, String iban, MultipartFile kycImage) throws IOException {
        TemporaryUserData tempUserData = new TemporaryUserData();
        tempUserData.setPartnerUserId(PartnerUserId);
        tempUserData.setMobileNumber(mobileNumber);
        boolean hasOptionalData = false;

        if (nationalCode != null) {
            tempUserData.setNationalCode(nationalCode);
            hasOptionalData = true;
        }
        if (postcode != null) {
            tempUserData.setPostcode(postcode);
            hasOptionalData = true;
        }
        if (birthdate != null) {
            tempUserData.setBirthdate(birthdate);
            hasOptionalData = true;
        }
        if (email != null) {
            tempUserData.setEmail(email);
            hasOptionalData = true;
        }
        if (bankCardNumber != null) {
            tempUserData.setBankCardNumber(bankCardNumber);
            hasOptionalData = true;
        }
        if (iban != null) {
            tempUserData.setIban(iban);
            hasOptionalData = true;
        }
        if (kycImage != null) {
            String kycImagePath = kycService.saveKycImage(kycImage);
            tempUserData.setKycImagePath(kycImagePath);
            hasOptionalData = true;
        }

        if (hasOptionalData) {
            temporaryUserDataRepository.save(tempUserData);
        }
    }

    //implement the logic to check if the user has already booked the rate
    public boolean hasUserBookedRateWithMobileNumberAndPartnerUserId(String mobileNumber, Long BusinessId) {
        String compositeKey = mobileNumber + ":" + BusinessId;
        return redisService.isBookingValidNumberOfTimes(null, compositeKey);
    }
}





