package com.amanatpay.onramp.service;

import com.amanatpay.onramp.dto.FinalRate;
import com.amanatpay.onramp.entity.PartnerBusiness;
import com.amanatpay.onramp.repository.PartnerBusinessRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * The PriceCalculationService class provides methods to calculate the weighted average price (WAP)
 * and apply commission to the rate based on the business ID.
 * The WAP is calculated based on the top 3 bids and asks from the order book.
 * The commission is retrieved from the partner business entity.
 * The commission can be either a percentage or a fixed value.
 * If the commission type is percentage, the rate is increased by the commission percentage.
 * If the commission type is fixed, the rate is increased by the fixed commission value.
 * The class also provides a method to calculate the WAP based on the amount to be exchanged.
 * The WAP is calculated based on the top asks until the total volume is equal to or greater than the amount.
 * The rate is then calculated as the weighted sum divided by the total volume.
 *
 */
@Service
public class PriceCalculationService {

    private final NobitexService nobitexService;
    private final PartnerBusinessRepository partnerBusinessRepository;
    private final SpreadService spreadService;
    private final TransactionsSettingDataService transactionsSettingDataService;

    /**
     * Constructor for the PriceCalculationService class.
     * The NobitexService and PartnerBusinessRepository instances are injected via constructor injection.
     *
     *
     * @param nobitexService the NobitexService instance
     * @param partnerBusinessRepository the PartnerBusinessRepository instance
     */
    public PriceCalculationService(NobitexService nobitexService, PartnerBusinessRepository partnerBusinessRepository, SpreadService spreadService, TransactionsSettingDataService transactionsSettingDataService) {
        this.nobitexService = nobitexService;
        this.partnerBusinessRepository = partnerBusinessRepository;
        this.spreadService = spreadService;
        this.transactionsSettingDataService = transactionsSettingDataService;
    }

    /**
     * Calculate the weighted average price (WAP) based on the top 3 bids and asks.
     * The WAP is calculated as the sum of the product of price and volume for the top 3 bids and asks,
     * divided by the total volume of the top 3 bids and asks.
     *
     *
     * @return the weighted average price
     */
    public BigDecimal calculateWAP() {
        Map<String, Object> orderBook = nobitexService.getOrderBook();

        // Cast the bids and asks as List<List<String>>
        List<List<String>> bids = castToListOfLists(orderBook.get("bids"));
        List<List<String>> asks = castToListOfLists(orderBook.get("asks"));

        BigDecimal totalVolume = BigDecimal.ZERO;
        BigDecimal weightedSum = BigDecimal.ZERO;

        // Calculate WAP for bids (top 3)
        for (int i = 0; i < Math.min(3, bids.size()); i++) {
            BigDecimal price = new BigDecimal(bids.get(i).get(0)); // First element is the price
            BigDecimal volume = new BigDecimal(bids.get(i).get(1)); // Second element is the volume
            totalVolume = totalVolume.add(volume);
            weightedSum = weightedSum.add(price.multiply(volume));
        }

        // Calculate WAP for asks (top 3)
        for (int i = 0; i < Math.min(3, asks.size()); i++) {
            BigDecimal price = new BigDecimal(asks.get(i).get(0)); // First element is the price
            BigDecimal volume = new BigDecimal(asks.get(i).get(1)); // Second element is the volume
            totalVolume = totalVolume.add(volume);
            weightedSum = weightedSum.add(price.multiply(volume));
        }

        return weightedSum.divide(totalVolume, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Cast the object to a List of Lists of Strings.
     * If the object is not an instance of List or the inner elements are not Lists, a ClassCastException is thrown.
     *
     *
     *
     * @param obj the object to cast
     * @return the object cast to a List of Lists of Strings
     */
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


    /**
     * Apply commission to the rate based on the business ID.
     * The commission is retrieved from the partner business entity.
     * The commission can be either a percentage or a fixed value.
     * If the commission type is percentage, the rate is increased by the commission percentage.
     * If the commission type is fixed, the rate is increased by the fixed commission value.
     *
     *
     * @param rate the rate to apply commission to
     * @param businessId the ID of the business
     * @return the rate with commission applied
     */
    public BigDecimal applyCommission(BigDecimal rate, Long businessId) {
        PartnerBusiness partnerBusiness = partnerBusinessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid business ID"));

        BigDecimal commissionValue = partnerBusiness.getCommissionValue();
        if ("PERCENTAGE".equalsIgnoreCase(partnerBusiness.getCommissionType())) {
            return rate.add(rate.multiply(commissionValue).divide(BigDecimal.valueOf(100)));
        } else if ("FIXED".equalsIgnoreCase(partnerBusiness.getCommissionType())) {
            return rate.add(commissionValue);
        } else {
            throw new IllegalArgumentException("Unknown commission type");
        }
    }


    /**
     * Calculate the weighted average price (WAP) based on the amount to be exchanged.
     * The WAP is calculated as the sum of the product of price and volume for the top asks,
     * until the total volume is equal to or greater than the amount to be exchanged.
     * The WAP is then calculated as the weighted sum divided by the total volume.
     *
     *
     * @param amount the amount to be exchanged
     * @return the weighted average price based on the amount
     */
    public BigDecimal calculateWAPByAmount(BigDecimal amount) {
        // Retrieve order book data from the database
        Map<String, Object> orderBook = nobitexService.getOrderBook();
        List<List<String>> bids = castToListOfLists(orderBook.get("bids"));

        BigDecimal totalVolume = BigDecimal.ZERO;
        BigDecimal weightedSum = BigDecimal.ZERO;

        for (List<String> bid : bids) {
            BigDecimal price = new BigDecimal(bid.get(0));
            BigDecimal volume = new BigDecimal(bid.get(1));

            if (totalVolume.add(volume).compareTo(amount) >= 0) {
                BigDecimal remainingVolume = amount.subtract(totalVolume);
                weightedSum = weightedSum.add(price.multiply(remainingVolume));
                totalVolume = totalVolume.add(remainingVolume);
                break;
            } else {
                weightedSum = weightedSum.add(price.multiply(volume));
                totalVolume = totalVolume.add(volume);
            }
        }

        if (totalVolume.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Order book does not have enough volume to fulfill the amount.");
        }

        return weightedSum.divide(totalVolume, MathContext.DECIMAL64);
    }

    public FinalRate calculateFinalRate(Long businessId, BigDecimal amount, BigDecimal defaultSystemFee, double defaultTransactionFee) {
        BigDecimal wap = calculateWAPByAmount(amount);
        BigDecimal rateWithSpread = spreadService.applyDynamicSpread(wap);
        BigDecimal finalRate = applyCommission(rateWithSpread, businessId);
        finalRate = finalRate.setScale(2, RoundingMode.UP);

        // Retrieve system fee from database or use default value
        BigDecimal systemFeePercentage = transactionsSettingDataService.findSettingValueByName("systemFee")
                .map(BigDecimal::new)
                .orElse(defaultSystemFee);

        BigDecimal systemFee = amount.multiply(systemFeePercentage);

        // Retrieve transaction fee from database or use default value
        BigDecimal transactionFee = transactionsSettingDataService.findSettingValueByName("transactionFee")
                .map(BigDecimal::new)
                .orElse(BigDecimal.valueOf(defaultTransactionFee));

        // Calculate the total amount the user will pay in IRT (Toman)
        //amount * finalRate + systemFee * finalRate IN TOMAN
        BigDecimal totalAmount = amount.multiply(finalRate).add(systemFee.multiply(finalRate));


        FinalRate finalRateDto = new FinalRate();
        finalRateDto.setRate(finalRate);
        finalRateDto.setSystemFee(systemFee);
        finalRateDto.setTransactionFee(transactionFee);
        finalRateDto.setTotalAmount(totalAmount);
        finalRateDto.setUsdtAmount(amount.subtract(transactionFee));

        return finalRateDto;
    }
}