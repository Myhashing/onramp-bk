package com.amanatpay.onramp.service;

import com.amanatpay.onramp.entity.TradeData;
import com.amanatpay.onramp.repository.TradeDataRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class TradeDataService {

    private final TradeDataRepository tradeDataRepository;

    public TradeDataService(TradeDataRepository tradeDataRepository) {
        this.tradeDataRepository = tradeDataRepository;
    }

    /**
     * Fetches the latest trade data from the database.
     *
     * @return the latest trade data
     */
    public List<TradeData> getLatestTradeData() {
        return tradeDataRepository.findTop10ByOrderByTimestampDesc();
    }

    /**
     * Fetches the trade data from the database.
     *
     * @return the trade data
     */
    public List<TradeData> getTradeData() {
        return tradeDataRepository.findAll();
    }

    /**
     * Fetches the trade data from the database by currency.
     *
     * @param currency the currency
     * @return the trade data
     */
    public List<TradeData> getTradeDataByCurrency(String currency) {
        return tradeDataRepository.findByCurrency(currency);
    }

    /**
     * Fetches the trade data from the database by time.
     *
     * @param time the time
     * @return the trade data
     */
    public List<TradeData> getTradeDataByTime(long time) {
        return tradeDataRepository.findByTime(time);
    }

    /**
     * Fetches the prices from the database by currency and time.
     *
     * @param currency the currency
     * @param time the time
     * @return the trade data
     */
    public List<TradeData> getPricesByCurrencyAndTime(String currency, long time) {
        return tradeDataRepository.findByCurrencyAndTime(currency, time);
    }



    /**
     * Fetches the market low from the database by currency and time range.
     *
     * @param currency the currency
     * @param startTime the start time
     * @param endTime the end time
     * @return the trade data
     */
    public List<BigDecimal> getMarketLowByCurrencyAndTimeRange(String currency, long startTime, long endTime) {
        return tradeDataRepository.findMarketLowByCurrencyAndTimeRange(currency, startTime, endTime);
    }


    /**
     * Fetches the market high from the database by currency and time range.
     *
     * @param currency the currency
     * @param startTime the start time
     * @param endTime the end time
     * @return the trade data
     */
    public List<BigDecimal> getMarketHighByCurrencyAndTimeRange(String currency, long startTime, long endTime) {
        return tradeDataRepository.findMarketHighByCurrencyAndTimeRange(currency, startTime, endTime);
    }

    /**
     * Fetches the market gains from the database by currency and time range.
     *
     * @param currency the currency
     * @param currentTime the start time
     * @param previousTime the end time
     * @return the trade data
     */
    public BigDecimal calculateMarketGains(String currency, long previousTime, long currentTime) {
        BigDecimal previousPrice = tradeDataRepository.findPriceByCurrencyAndTime(currency, previousTime);
        BigDecimal currentPrice = tradeDataRepository.findPriceByCurrencyAndTime(currency, currentTime);

        if (previousPrice != null && currentPrice != null && previousPrice.compareTo(BigDecimal.ZERO) != 0) {
            return currentPrice.subtract(previousPrice)
                              .divide(previousPrice, RoundingMode.HALF_UP)
                              .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }

    public List<BigDecimal> getPricesByCurrencyAndTimeRange(String usdtirt, long l, long l1) {
        return tradeDataRepository.findPricesByCurrencyAndTimeRange(usdtirt, l, l1);
    }
}


