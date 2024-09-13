package com.amanatpay.onramp.service;

import com.amanatpay.onramp.entity.TransactionsSettingData;
import com.amanatpay.onramp.repository.TransactionsSettingDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionsSettingDataService {

    @Autowired
    private TransactionsSettingDataRepository repository;

    public List<TransactionsSettingData> findAll() {
        return repository.findAll();
    }

    public TransactionsSettingData findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public TransactionsSettingData save(TransactionsSettingData data) {
        return repository.save(data);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public Optional<String> findSettingValueByName(String settingName) {
        return repository.findBySettingName(settingName).map(TransactionsSettingData::getSettingValue);
    }

    public Optional<TransactionsSettingData> findBySettingName(String settingName) {
        return repository.findBySettingName(settingName);
    }


}