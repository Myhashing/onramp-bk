package com.amanatpay.onramp.repository;

import com.amanatpay.onramp.entity.TransactionsSettingData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionsSettingDataRepository extends JpaRepository<TransactionsSettingData, UUID> {
    Optional<TransactionsSettingData> findBySettingName(String settingName);
}