package com.amanatpay.onramp.service;

import com.amanatpay.onramp.dto.RateBooking;
import com.amanatpay.onramp.entity.TempSaveBooking;
import com.amanatpay.onramp.entity.TemporaryUserData;
import com.amanatpay.onramp.exception.BookingExpiredException;
import com.amanatpay.onramp.repository.TempSaveBookingRepository;
import com.amanatpay.onramp.repository.TemporaryUserDataRepository;
import com.amanatpay.onramp.service.kycService.KycService;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class RateBookingService {

    private final RedisService redisService;

    private final TemporaryUserDataRepository temporaryUserDataRepository;
    private final KycService kycService;
    private final TempSaveBookingRepository tempSaveBookingRepository;

    public RateBookingService(RedisService redisService, TemporaryUserDataRepository temporaryUserDataRepository, KycService kycService, TempSaveBookingRepository tempSaveBookingRepository) {
        this.redisService = redisService;
        this.temporaryUserDataRepository = temporaryUserDataRepository;
        this.kycService = kycService;
        this.tempSaveBookingRepository = tempSaveBookingRepository;
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
        tempUserData.setNationalCode(nationalCode);

        boolean hasOptionalData = false;


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


    /**
     * Saves a rate booking to the temporary booking repository.
     *
     * @param booking The RateBooking object containing the booking details to be saved.
     *
     * @return the TempSaveBooking object containing the saved booking details
     */
    public TempSaveBooking saveBooking(RateBooking booking) {
        TempSaveBooking tempSaveBooking = new TempSaveBooking();
        tempSaveBooking.setBookingId(booking.getBookingId());
        tempSaveBooking.setPartnerUserId(booking.getPartnerUserId());
        tempSaveBooking.setRate(booking.getRate());
        tempSaveBooking.setAmount(booking.getAmount());
        tempSaveBooking.setExpirationTime(booking.getExpirationTime());
        tempSaveBooking.setMobileNumber(booking.getMobileNumber());
        tempSaveBooking.setBusinessId(booking.getBusinessId());
        tempSaveBooking.setWalletAddress(booking.getWalletAddress());
        tempSaveBooking.setNationalCode(booking.getNationalCode());
        return tempSaveBookingRepository.save(tempSaveBooking);
    }

    public TempSaveBooking getTempSaveBooking(@NotNull String bookingId) {
        return tempSaveBookingRepository.findByBookingId(bookingId);
    }
}





