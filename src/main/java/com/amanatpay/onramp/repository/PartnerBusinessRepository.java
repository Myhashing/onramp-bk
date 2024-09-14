package com.amanatpay.onramp.repository;

import com.amanatpay.onramp.entity.PartnerBusiness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PartnerBusinessRepository extends JpaRepository<PartnerBusiness, Long> {
        @Query("SELECT pb.language FROM PartnerBusiness pb WHERE pb.id = :businessId")
        Optional<String> findLanguageByBusinessId(@Param("businessId") Long businessId);
}