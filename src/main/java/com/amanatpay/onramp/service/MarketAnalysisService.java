package com.amanatpay.onramp.service;

import com.amanatpay.onramp.dto.AlertCategory;
import com.amanatpay.onramp.dto.AlertLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Map;

@Service
public class MarketAnalysisService {

    private final NotificationService notificationService;
    @Value("${market.volatility.threshold}")
    private BigDecimal volatilityThreshold;
    @Value("${market.atr.threshold}")
    private BigDecimal atrThreshold;
    @Value("${market.rsi.overbought}")
    private int rsiOverbought;
    @Value("${market.rsi.oversold}")
    private int rsiOversold;

    public MarketAnalysisService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Volatility Detection and Alerts

    /**
     * Calculate the standard deviation of a list of prices.
     *
     * @param prices the list of prices
     * @return the standard deviation
     */
    public BigDecimal calculateStandardDeviation(List<BigDecimal> prices) {
        BigDecimal mean = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(prices.size()), MathContext.DECIMAL128);
        BigDecimal variance = prices.stream()
                .map(price -> price.subtract(mean).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(prices.size()), MathContext.DECIMAL128);
        return variance.sqrt(MathContext.DECIMAL128);
    }

    public void checkStandardDeviation(BigDecimal standardDeviation) {
        if (standardDeviation.compareTo(volatilityThreshold) > 0) {
            notificationService.alertAdmin("High volatility detected", AlertLevel.HIGH, AlertCategory.FINANCE);
        }
    }

    /**
     * Calculate the Average True Range (ATR) of a list of high and low prices.
     *
     * @param highs the list of high prices
     * @param lows  the list of low prices
     * @return the Average True Range
     */
    public BigDecimal calculateATR(List<BigDecimal> highs, List<BigDecimal> lows) {
        BigDecimal atr = BigDecimal.ZERO;
        for (int i = 0; i < highs.size(); i++) {
            atr = atr.add(highs.get(i).subtract(lows.get(i)));
        }
        return atr.divide(new BigDecimal(highs.size()), MathContext.DECIMAL128);
    }

    /**
     * Check if the Average True Range (ATR) exceeds the threshold.
     *
     * @param atr the Average True Range
     */
    public void checkVolatility(BigDecimal standardDeviation) {
        if (standardDeviation.compareTo(volatilityThreshold) > 0) {
            notificationService.alertAdmin("High volatility detected", AlertLevel.HIGH, AlertCategory.FINANCE);
        }
    }

    /**
     * Check if the Average True Range (ATR) exceeds the threshold.
     *
     * @param atr the Average True Range
     */
    public void checkATR(BigDecimal atr) {
        if (atr.compareTo(atrThreshold) > 0) {
            notificationService.alertAdmin("Large price movement detected", AlertLevel.HIGH, AlertCategory.FINANCE);
        }
    }

    // Market Trend Indicators
    public BigDecimal calculateSMA(List<BigDecimal> prices) {
        return prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(prices.size()), MathContext.DECIMAL128);
    }

    public BigDecimal calculateEMA(List<BigDecimal> prices, int period) {
        BigDecimal k = new BigDecimal(2).divide(new BigDecimal(period + 1), MathContext.DECIMAL128);
        BigDecimal ema = prices.get(0);
        for (int i = 1; i < prices.size(); i++) {
            ema = prices.get(i).multiply(k).add(ema.multiply(BigDecimal.ONE.subtract(k)));
        }
        return ema;
    }

    // Relative Strength Index (RSI)
    public BigDecimal calculateRSI(List<BigDecimal> gains, List<BigDecimal> losses) {
        BigDecimal averageGain = gains.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(gains.size()), MathContext.DECIMAL128);
        BigDecimal averageLoss = losses.stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(losses.size()), MathContext.DECIMAL128);
        BigDecimal rs = averageGain.divide(averageLoss, MathContext.DECIMAL128);
        return BigDecimal.valueOf(100).subtract(BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), MathContext.DECIMAL128));
    }

    public void checkRSI(BigDecimal rsi) {
        if (rsi.compareTo(BigDecimal.valueOf(rsiOverbought)) > 0) {
            notificationService.alertAdmin("RSI indicates overbought conditions", AlertLevel.HIGH, AlertCategory.FINANCE);
        } else if (rsi.compareTo(BigDecimal.valueOf(rsiOversold)) < 0) {
            notificationService.alertAdmin("RSI indicates oversold conditions", AlertLevel.HIGH, AlertCategory.FINANCE);
        }
    }

    // Risk Management
    public BigDecimal calculateVaR(BigDecimal expectedReturn, BigDecimal standardDeviation, BigDecimal zScore) {
        return expectedReturn.subtract(standardDeviation.multiply(zScore));
    }

    // Order Book Analysis
    public BigDecimal calculateWAP(List<Map<String, BigDecimal>> orderBook) {
        BigDecimal totalVolume = BigDecimal.ZERO;
        BigDecimal weightedPriceSum = BigDecimal.ZERO;
        for (Map<String, BigDecimal> order : orderBook) {
            BigDecimal price = order.get("price");
            BigDecimal volume = order.get("volume");
            totalVolume = totalVolume.add(volume);
            weightedPriceSum = weightedPriceSum.add(price.multiply(volume));
        }
        return weightedPriceSum.divide(totalVolume, MathContext.DECIMAL128);
    }

    // Historical Data Handling
    public void fetchAndStoreHistoricalData() {
        // Implementation to fetch and store historical data


    }

}