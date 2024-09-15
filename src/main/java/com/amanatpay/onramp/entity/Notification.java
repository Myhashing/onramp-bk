package com.amanatpay.onramp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
public class Notification extends Auditable{

    @Id
    @GeneratedValue
    private UUID id;

    private String message;

    private String recipient;

    private String channel;

    private Timestamp timestamp;

}