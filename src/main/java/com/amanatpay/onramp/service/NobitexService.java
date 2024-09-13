package com.amanatpay.onramp.service;

import com.amanatpay.onramp.dto.NobitexResponse;
import com.amanatpay.onramp.entity.NobitexOrderBookData;
import com.amanatpay.onramp.entity.TradeData;
import com.amanatpay.onramp.repository.NobitexOrderBookDataRepository;
import com.amanatpay.onramp.repository.TradeDataRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


/**
 * Service class for fetching data from the Nobitex API.
 * This class fetches order book and trade data from the Nobitex API and stores it in the database.
 * It also provides methods to retrieve the latest order book and trade data.
 * The data is fetched every 2 seconds using a scheduled task.
 * The fetched data is stored in the database using the NobitexOrderBookData and TradeData entities.
 */
@Service
public class NobitexService {

    private final RestTemplate restTemplate;
    private final NobitexOrderBookDataRepository nobitexOrderBookDataRepository;
    private final TradeDataRepository tradeDataRepository;
    private final ObjectMapper objectMapper;

    @Value("${nobitex.api.base-url}")
    private String nobitexApiUrl;


    /**
     * Constructs a new NobitexService with the specified RestTemplate, NobitexOrderBookDataRepository, TradeDataRepository, and ObjectMapper.
     *
     * @param restTemplate                   the RestTemplate to be used by this NobitexService
     * @param nobitexOrderBookDataRepository the NobitexOrderBookDataRepository to be used by this NobitexService
     * @param tradeDataRepository            the TradeDataRepository to be used by this NobitexService
     * @param objectMapper                   the ObjectMapper to be used by this NobitexService
     */
    public NobitexService(RestTemplate restTemplate, NobitexOrderBookDataRepository nobitexOrderBookDataRepository, TradeDataRepository tradeDataRepository, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.nobitexOrderBookDataRepository = nobitexOrderBookDataRepository;
        this.tradeDataRepository = tradeDataRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Fetches order book and trade data from the Nobitex API.
     * This method is scheduled to run every 2 seconds.
     * It fetches order book and trade data from the Nobitex API and stores it in the database.
     */
    @Scheduled(fixedRate = 2000) // Fetch data every 2 seconds
    public void fetchData() {
        fetchOrderBook();
        fetchTradeData();
    }

    /**
     * Fetches order book data from the Nobitex API and stores it in the database.
     * This method is called by the fetchData() method.
     * It fetches order book data from the Nobitex API and stores it in the database using the NobitexOrderBookData entity.
     * The fetched data is converted to JSON format before storing it in the database.
     * The timestamp of the data is set to the current time.
     * The data is stored in the nobitex_order_book_data table.
     */
    public void fetchOrderBook() {
        String url = nobitexApiUrl + "/orderbook/USDTIRT";
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

    /**
     * Fetches trade data from the Nobitex API and stores it in the database.
     * This method is called by the fetchData() method.
     * It fetches trade data from the Nobitex API and stores it in the database using the TradeData entity.
     * The fetched data is converted to JSON format before storing it in the database.
     * The timestamp of the data is set to the current time.
     */
    public void fetchTradeData() {
        String url = nobitexApiUrl + "/trades/USDTIRT";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        assert response != null;
        if ("ok".equals(response.get("status"))) {
            List<Map<String, Object>> trades = (List<Map<String, Object>>) response.get("trades");
            for (Map<String, Object> trade : trades) {
                long time = (long) trade.get("time");
                BigDecimal price = new BigDecimal((String) trade.get("price"));
                BigDecimal volume = new BigDecimal((String) trade.get("volume"));
                String type = (String) trade.get("type");

                TradeData tradeDataEntity = new TradeData();
                tradeDataEntity.setId(UUID.randomUUID());
                tradeDataEntity.setTime(time);
                tradeDataEntity.setPrice(price);
                tradeDataEntity.setVolume(volume);
                tradeDataEntity.setType(type);
                tradeDataEntity.setCurrency("USDTIRT");
                tradeDataEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));
                tradeDataRepository.save(tradeDataEntity);

            }
        }
    }


    /**
     * Retrieves the latest trade data from the database.
     * This method retrieves the latest trade data from the database using the TradeDataRepository.
     * It returns the trade data as a Map<String, Object> object.
     * The data is stored in the trade_data table.
     *
     * @return the latest trade data as a Map<String, Object> object
     */
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

    public void processNobitexData(NobitexResponse response) {
        if ("ok".equals(response.getStatus())) {
            for (NobitexResponse.Trade trade : response.getTrades()) {
                TradeData tradeData = new TradeData();
                tradeData.setId(UUID.randomUUID());
                tradeData.setTime(trade.getTime());
                tradeData.setPrice(new BigDecimal(trade.getPrice()));
                tradeData.setVolume(new BigDecimal(trade.getVolume()));
                tradeData.setType(trade.getType());
                tradeData.setCurrency("IRR"); // Set the currency as per your requirement
                tradeData.setTimestamp(Timestamp.from(Instant.ofEpochMilli(trade.getTime())));

                tradeDataRepository.save(tradeData);
            }
        }
    }


}