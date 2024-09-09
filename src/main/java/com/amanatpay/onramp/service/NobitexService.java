package com.amanatpay.onramp.service;

import com.amanatpay.onramp.entity.NobitexOrderBookData;
import com.amanatpay.onramp.entity.TradeData;
import com.amanatpay.onramp.repository.NobitexOrderBookDataRepository;
import com.amanatpay.onramp.repository.TradeDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class NobitexService {

    private final RestTemplate restTemplate;
    private final NobitexOrderBookDataRepository nobitexOrderBookDataRepository;
    private final TradeDataRepository tradeDataRepository;
    private final ObjectMapper objectMapper;

    public NobitexService(RestTemplate restTemplate, NobitexOrderBookDataRepository nobitexOrderBookDataRepository, TradeDataRepository tradeDataRepository, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.nobitexOrderBookDataRepository = nobitexOrderBookDataRepository;
        this.tradeDataRepository = tradeDataRepository;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 2000) // Fetch data every 2 seconds
    public void fetchData() {
        fetchOrderBook();
        fetchTradeData();
    }

    public void fetchOrderBook() {
        String url = "https://api.nobitex.ir/v2/orderbook/USDTIRT";
        Map<String, Object> orderBook = restTemplate.getForObject(url, Map.class);

        try {
            String jsonData = objectMapper.writeValueAsString(orderBook);
            NobitexOrderBookData nobitexOrderBookData = new NobitexOrderBookData();
            nobitexOrderBookData.setId(UUID.randomUUID());
            nobitexOrderBookData.setData(jsonData);
            nobitexOrderBookData.setTimestamp(new Timestamp(System.currentTimeMillis()));
            nobitexOrderBookDataRepository.save(nobitexOrderBookData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void fetchTradeData() {
        String url = "https://api.nobitex.ir/v2/trades/USDTIRT";
        Map<String, Object> tradeData = restTemplate.getForObject(url, Map.class);

        try {
            String jsonData = objectMapper.writeValueAsString(tradeData);
            TradeData tradeDataEntity = new TradeData();
            tradeDataEntity.setId(UUID.randomUUID());
            tradeDataEntity.setData(jsonData);
            tradeDataEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));
            tradeDataRepository.save(tradeDataEntity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getOrderBook() {
        Optional<NobitexOrderBookData> latestOrderBook = nobitexOrderBookDataRepository.findTopByOrderByTimestampDesc();
        if (latestOrderBook.isPresent()) {
            String jsonData = latestOrderBook.get().getData();
            try {
                // Convert JSON string back to Map
                return objectMapper.readValue(jsonData, Map.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}