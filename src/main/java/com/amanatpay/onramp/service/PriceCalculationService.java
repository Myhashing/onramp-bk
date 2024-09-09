package com.amanatpay.onramp.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PriceCalculationService {

    private final NobitexService nobitexService;

    public PriceCalculationService(NobitexService nobitexService) {
        this.nobitexService = nobitexService;
    }

    public BigDecimal calculateWAP() {
        Map<String, Object> orderBook = nobitexService.getOrderBook();

        // Cast the bids and asks as List<List<String>>
        List<List<String>> bids = castToListOfLists(orderBook.get("bids"));
        List<List<String>> asks = castToListOfLists(orderBook.get("asks"));

        BigDecimal totalVolume = BigDecimal.ZERO;
        BigDecimal weightedSum = BigDecimal.ZERO;

        // Calculate WAP for bids
        for (int i = 0; i < Math.min(3, bids.size()); i++) {
            BigDecimal price = new BigDecimal(bids.get(i).get(0)); // First element is the price
            BigDecimal volume = new BigDecimal(bids.get(i).get(1)); // Second element is the volume
            totalVolume = totalVolume.add(volume);
            weightedSum = weightedSum.add(price.multiply(volume));
        }

        // Calculate WAP for asks
        for (int i = 0; i < Math.min(3, asks.size()); i++) {
            BigDecimal price = new BigDecimal(asks.get(i).get(0)); // First element is the price
            BigDecimal volume = new BigDecimal(asks.get(i).get(1)); // Second element is the volume
            totalVolume = totalVolume.add(volume);
            weightedSum = weightedSum.add(price.multiply(volume));
        }

        return weightedSum.divide(totalVolume, BigDecimal.ROUND_HALF_UP);
    }

    private List<List<String>> castToListOfLists(Object obj) {
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            if (!list.isEmpty() && list.get(0) instanceof List) {
                return (List<List<String>>) list;
            } else {
                throw new ClassCastException("Expected a List of Lists but got: " + (list.isEmpty() ? "empty list" : list.get(0).getClass().getName()));
            }
        }
        throw new ClassCastException("Expected a List but got: " + obj.getClass().getName());
    }

    public BigDecimal applyDynamicSpread(BigDecimal wap) {
        BigDecimal spread = BigDecimal.valueOf(2000); // Example spread
        return wap.add(spread);
    }
}
