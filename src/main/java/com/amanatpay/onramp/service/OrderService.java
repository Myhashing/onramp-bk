package com.amanatpay.onramp.service;

import com.amanatpay.onramp.entity.Order;
import com.amanatpay.onramp.entity.OrderStatus;
import com.amanatpay.onramp.repository.OrderRepository;
import com.amanatpay.onramp.dto.OrderRequest;
import com.amanatpay.onramp.dto.KycUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    public Order createOrder(OrderRequest request) {
        // Validate KYC level
        KycUpdateRequest kyc = userService.getKyc(request.getUserId());
        if (kyc.getKycLevel() < requiredKycLevel(request.getFiatAmount())) {
            throw new IllegalArgumentException("Insufficient KYC level");
        }

        // Calculate fees and exchange rate
        BigDecimal exchangeRate = getExchangeRate(request.getCryptocurrency());
        BigDecimal fees = calculateFees(request.getFiatAmount());

        Order order = new Order();
        order.setOrderId(UUID.randomUUID());
        order.setUserId(request.getUserId());
        order.setBusinessId(request.getBusinessId());
        order.setCryptocurrency(request.getCryptocurrency());
        order.setCryptoAmount(request.getFiatAmount().divide(exchangeRate, BigDecimal.ROUND_HALF_UP));
        order.setFiatAmount(request.getFiatAmount());
        order.setExchangeRate(exchangeRate);
        order.setFees(fees);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return orderRepository.save(order);
    }

    public Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    public void updateOrderStatus(UUID orderId, OrderStatus status) {
        Order order = getOrder(orderId);
        order.setStatus(status);
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        orderRepository.save(order);
    }

    private BigDecimal getExchangeRate(String cryptocurrency) {
        // Implement exchange rate retrieval logic
        return BigDecimal.valueOf(50000); // Example rate
    }

    private BigDecimal calculateFees(BigDecimal fiatAmount) {
        // Implement fee calculation logic
        return fiatAmount.multiply(BigDecimal.valueOf(0.01)); // Example fee
    }

    private int requiredKycLevel(BigDecimal fiatAmount) {
        // Implement KYC level requirement logic
        return fiatAmount.compareTo(BigDecimal.valueOf(1000)) > 0 ? 2 : 1;
    }
}