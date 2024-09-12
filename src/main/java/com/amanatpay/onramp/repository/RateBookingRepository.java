package com.amanatpay.onramp.repository;

import com.amanatpay.onramp.entity.RateBooking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RateBookingRepository extends JpaRepository<RateBooking, String> {
}