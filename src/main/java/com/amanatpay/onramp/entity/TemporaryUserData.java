package com.amanatpay.onramp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "temporary_user_data")
@Data
public class TemporaryUserData extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "partner_user_id", nullable = false)
    @NotNull
    private Long partnerUserId;

    @Column(name = "mobile_number", nullable = false)
    @NotNull
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid mobile number")
    private String mobileNumber;

    @Column(name = "national_code")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid national code")
    private String nationalCode;

    @Column(name = "postcode")
    @Pattern(regexp = "^[0-9]{5,10}$", message = "Invalid postcode")
    private String postcode;

    @Column(name = "kyc_image_path")
    @Size(max = 255, message = "KYC image path too long")
    private String kycImagePath;

    @Column(name = "birthdate")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Invalid birthdate format")
    private String birthdate;

    @Column(name = "email")
    @Email(message = "Invalid email address")
    private String email;

    @Column(name = "iban")
    @Pattern(regexp = "^[A-Z0-9]{15,34}$", message = "Invalid IBAN")
    private String iban;

    @Column(name = "bank_card_number")
    @Pattern(regexp = "^[0-9]{16}$", message = "Invalid bank card number")
    private String bankCardNumber;


}