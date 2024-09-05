package com.amanatpay.onramp.repository;

import com.amanatpay.onramp.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BusinessRepository extends JpaRepository<Business, UUID> {
}