package com.amanatpay.onramp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Table(name = "businesses")
@Data
public class Business {

    @Id
    private UUID id;

    @NotBlank(message = "Business name is required")
    @Size(max = 255, message = "Business name must be less than 255 characters")
    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "business_owner_id", nullable = false)
    private UUID businessOwnerId;

    @Size(max = 255, message = "Website URL must be less than 255 characters")
    @Pattern(regexp = "^(http|https)://.*$", message = "Website URL must be a valid URL")
    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
}