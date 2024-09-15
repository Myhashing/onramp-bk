package com.amanatpay.onramp.repository;

import com.amanatpay.onramp.entity.TempSaveBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TempSaveBookingRepository extends JpaRepository<TempSaveBooking, UUID> {
}