package com.amanatpay.onramp.controller;

import com.amanatpay.onramp.entity.TransactionsSettingData;
import com.amanatpay.onramp.service.TransactionsSettingDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions-settings")
public class TransactionsSettingDataController {

    @Autowired
    private TransactionsSettingDataService service;

    @GetMapping
    public List<TransactionsSettingData> getAllSettings() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public TransactionsSettingData getSettingById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public TransactionsSettingData createSetting(@RequestBody TransactionsSettingData data) {
        return service.save(data);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public TransactionsSettingData updateSetting(@PathVariable UUID id, @RequestBody TransactionsSettingData data) {
        data.setId(id);
        return service.save(data);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteSetting(@PathVariable UUID id) {
        service.deleteById(id);
    }
}