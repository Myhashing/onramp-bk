package com.amanatpay.onramp.service.marketService;

import com.amanatpay.onramp.service.TradeDataService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MarketMonitoringService {

    private final MarketAnalysisService marketAnalysisService;
    private final TradeDataService tradeDataService;

    public MarketMonitoringService(MarketAnalysisService marketAnalysisService, TradeDataService tradeDataService) {
        this.marketAnalysisService = marketAnalysisService;
        this.tradeDataService = tradeDataService;
    }

    /**
     * Performs market analysis.
     * This method is scheduled to run every 60 seconds.
     * It fetches market data from the TradeDataService and performs various calculations and checks.
     * It calculates the EMA, SMA, standard deviation, ATR, gains, and losses.
     * It checks for high volatility, large price movement, and other conditions.
     * It sends alerts to the NotificationService if any conditions are met.
     */
    //TODO: after check the analysis run it
//    @Scheduled(fixedRate = 60000) // Runs every 60 seconds
    public void performMarketAnalysis() {
        // Fetch market data
        List<BigDecimal> prices = tradeDataService.getPricesByCurrencyAndTimeRange("USDTIRT", System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000, System.currentTimeMillis());

        List<BigDecimal> lows = tradeDataService.getMarketLowByCurrencyAndTimeRange("USDTIRT", System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000, System.currentTimeMillis());
        List<BigDecimal> highs = tradeDataService.getMarketHighByCurrencyAndTimeRange("USDTIRT", System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000, System.currentTimeMillis());
        // Calculate gains and losses
        BigDecimal gains = tradeDataService.calculateMarketGains("USDTIRT", System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000, System.currentTimeMillis());
        BigDecimal losses = gains.compareTo(BigDecimal.ZERO) < 0 ? gains.abs() : BigDecimal.ZERO;
        gains = gains.compareTo(BigDecimal.ZERO) > 0 ? gains : BigDecimal.ZERO;

        // Calculate EMA
        BigDecimal ema = marketAnalysisService.calculateEMA(prices, 14);

        // Calculate SMA
        BigDecimal sma = marketAnalysisService.calculateSMA(prices);

        // Calculate standard deviation
        BigDecimal standardDeviation = marketAnalysisService.calculateStandardDeviation(prices);
        marketAnalysisService.checkStandardDeviation(standardDeviation);

        // Calculate ATR
        BigDecimal atr = marketAnalysisService.calculateATR(highs, lows);
        marketAnalysisService.checkATR(atr);

        // Check for high volatility
        marketAnalysisService.checkVolatility(standardDeviation);

        // Check for large price movement
        marketAnalysisService.checkATR(atr);
    }


}