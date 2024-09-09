package com.amanatpay.onramp.repository;

import com.amanatpay.onramp.entity.OrderBookData;
import com.amanatpay.onramp.entity.TradeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TradeDataRepository extends JpaRepository<TradeData, UUID> {
}