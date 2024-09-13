package com.amanatpay.onramp.repository;

import com.amanatpay.onramp.entity.TradeData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface TradeDataRepository extends JpaRepository<TradeData, UUID> {

    boolean existsByTimeAndCurrency(long time, String currency);

    List<TradeData> findTop10ByOrderByTimestampDesc();

    List<TradeData> findByCurrency(String currency);

    List<TradeData> findByTime(long time);

    List<TradeData> findByCurrencyAndTime(String currency, long time);

    @Query("SELECT MIN(t.price) FROM TradeData t WHERE t.currency = :currency AND t.time BETWEEN :startTime AND :endTime")
    List<BigDecimal> findMarketLowByCurrencyAndTimeRange(@Param("currency") String currency, @Param("startTime") long startTime, @Param("endTime") long endTime);

    @Query("SELECT t.price FROM TradeData t WHERE t.currency = :currency AND t.time = :time")
    BigDecimal findPriceByCurrencyAndTime(@Param("currency") String currency, @Param("time") long time);

    @Query("SELECT MAX(t.price) FROM TradeData t WHERE t.currency = :currency AND t.time BETWEEN :startTime AND :endTime")
    List<BigDecimal> findMarketHighByCurrencyAndTimeRange(@Param("currency") String currency, @Param("startTime") long startTime, @Param("endTime") long endTime);

    @Query("SELECT t.price FROM TradeData t WHERE t.currency = :currency AND t.time BETWEEN :startTime AND :endTime")
    List<BigDecimal> findPricesByCurrencyAndTimeRange(@Param("currency") String currency, @Param("startTime") long startTime, @Param("endTime") long endTime);
}


