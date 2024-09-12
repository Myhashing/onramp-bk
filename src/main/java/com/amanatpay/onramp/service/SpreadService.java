package com.amanatpay.onramp.service;

import com.amanatpay.onramp.entity.TradeData;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Map;

/**
 * The SpreadService class provides methods to calculate the dynamic spread
 * based on market volatility and liquidity.
 */
@Service
public class SpreadService {

    private final NobitexService nobitexService;

    @Value("${spread.base}")
    private BigDecimal baseSpreadDefault;

    private final TradeDataService tradeDataService;

    private final MarketAnalysisService marketAnalysisService;


    /**
     * Constructor for the SpreadService class.
     *
     * @param nobitexService the NobitexService instance
     */
    public SpreadService(NobitexService nobitexService, TradeDataService tradeDataService, MarketAnalysisService marketAnalysisService) {
        this.nobitexService = nobitexService;
        this.tradeDataService = tradeDataService;
        this.marketAnalysisService = marketAnalysisService;
    }


    /**
     * Apply a dynamic spread to the weighted average price (WAP).
     * The dynamic spread is calculated based on market volatility and liquidity.
     * The higher the volatility, the higher the spread.
     * The lower the liquidity, the higher the spread.
     *
     * @param wap the weighted average price
     * @return the WAP with the dynamic spread applied
     */
    public BigDecimal applyDynamicSpread(@NotNull BigDecimal wap) {
        // Get the current order book and historical trades
        Map<String, Object> orderBook = nobitexService.getOrderBook();
        List<List<String>> bids = (List<List<String>>) orderBook.get("bids");
        List<List<String>> asks = (List<List<String>>) orderBook.get("asks");

        // Get recent trade history data
        List<TradeData> trades =  tradeDataService.getLatestTradeData();
        // Calculate market volatility
        BigDecimal volatility = calculateVolatility(trades);

        // Calculate liquidity score (based on top bid/ask volumes)
        BigDecimal liquidityScore = calculateLiquidityScore(bids, asks);

        //TODO: Implement a dynamic base spread calculation
        BigDecimal baseSpread = baseSpreadDefault;

        // Dynamic spread calculation
        BigDecimal dynamicSpread = baseSpread
                .add(volatility.multiply(BigDecimal.valueOf(100))) // increase spread based on volatility
                .subtract(liquidityScore.multiply(BigDecimal.valueOf(50))); // decrease spread based on liquidity

        // Ensure the spread doesn't drop below a minimum value
        BigDecimal minimumSpread = BigDecimal.valueOf(500); // Minimum spread
        dynamicSpread = dynamicSpread.max(minimumSpread);

        // Apply the spread to the WAP
        return wap.add(dynamicSpread, MathContext.DECIMAL64);
    }


    /**
     * Calculate the volatility of recent trades.
     * Volatility is calculated as the standard deviation of trade prices.
     * The higher the standard deviation, the higher the volatility.
     * The lower the standard deviation, the lower the volatility.
     *
     * @param trades a list of trade data
     *               each trade data is a map containing price and volume
     *               e.g., {"price": "1234.56", "volume": "0.1234"}
     * @return the volatility as a BigDecimal
     * representing the standard deviation of trade prices
     * or zero if the trade data is empty or null
     * or the standard deviation cannot be calculated
     * (e.g., division by zero, arithmetic exceptions)
     */
    private BigDecimal calculateVolatility(List<TradeData> trades) {
        // Calculate standard deviation of recent trades
        if (trades == null || trades.isEmpty()) return BigDecimal.ZERO;

        BigDecimal mean = trades.stream()
                .map(TradeData::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(trades.size()), MathContext.DECIMAL64);

        BigDecimal variance = trades.stream()
                .map(trade -> trade.getPrice().subtract(mean).pow(2))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(trades.size()), MathContext.DECIMAL64);

        return variance.sqrt(MathContext.DECIMAL64); // Volatility as standard deviation
    }

    /**
     * Calculate the liquidity score based on the volume of top bids and asks.
     * The liquidity score is the sum of the volumes of the top 3 bids and asks.
     * The higher the volume, the higher the liquidity score.
     *
     * @param bids a list of bid data
     *             each bid data is a list containing price and volume
     *             e.g., ["1234.56", "0.1234"]
     *             The list is sorted in descending order of price.
     *             The first element is the highest bid price.
     *             The second element is the volume of the highest bid.
     *             The third element is the second-highest bid price, and so on.
     *             <p>
     *             Example:
     *             [
     *             ["1234.56", "0.1234"],
     *             ["1234.55", "0.2345"],
     *             ["1234.54", "0.3456"]
     *             ]
     *             <p>
     *             In this example, the highest bid price is 1234.56 with a volume of 0.1234.
     *             The second-highest bid price is 1234.55 with a volume of 0.2345, and so on.
     * @return the liquidity score as a BigDecimal
     * representing the sum of the volumes of the top 3 bids and asks
     * or zero if the bid data is empty or null
     * or the volume cannot be calculated
     * (e.g., invalid data format, arithmetic exceptions)
     */
    private BigDecimal calculateLiquidityScore(List<List<String>> bids, List<List<String>> asks) {
        // Calculate a liquidity score based on the volume of top bids and asks
        BigDecimal totalBidVolume = BigDecimal.ZERO;
        BigDecimal totalAskVolume = BigDecimal.ZERO;

        // Sum the top 3 levels of bid and ask volumes
        for (int i = 0; i < Math.min(3, bids.size()); i++) {
            totalBidVolume = totalBidVolume.add(new BigDecimal(bids.get(i).get(1))); // Volume of bids
        }

        for (int i = 0; i < Math.min(3, asks.size()); i++) {
            totalAskVolume = totalAskVolume.add(new BigDecimal(asks.get(i).get(1))); // Volume of asks
        }

        // Liquidity score: Higher volume means higher liquidity, reduce the spread
        BigDecimal liquidityScore = totalBidVolume.add(totalAskVolume)
                .divide(BigDecimal.valueOf(2), MathContext.DECIMAL64); // Averaging both sides

        return liquidityScore;
    }

}
