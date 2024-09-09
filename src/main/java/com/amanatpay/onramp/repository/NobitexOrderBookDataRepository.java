package com.amanatpay.onramp.repository;

import com.amanatpay.onramp.entity.NobitexOrderBookData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NobitexOrderBookDataRepository extends JpaRepository<NobitexOrderBookData, UUID> {
    Optional<NobitexOrderBookData> findTopByOrderByTimestampDesc();
}