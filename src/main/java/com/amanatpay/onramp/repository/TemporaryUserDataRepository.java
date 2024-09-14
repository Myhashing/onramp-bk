package com.amanatpay.onramp.repository;

import com.amanatpay.onramp.entity.TemporaryUserData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TemporaryUserDataRepository extends JpaRepository<TemporaryUserData, UUID> {
}