package com.amanatpay.onramp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class TransactionsSettingData extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String settingName;
    private String settingValue;
    private String description;


}
