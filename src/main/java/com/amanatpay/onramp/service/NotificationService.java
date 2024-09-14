package com.amanatpay.onramp.service;

import com.amanatpay.onramp.dto.AlertCategory;
import com.amanatpay.onramp.dto.AlertLevel;
import com.amanatpay.onramp.entity.Notification;
import com.amanatpay.onramp.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final EmailNotificationProvider emailNotificationProvider;

    private final SMSNotificationProvider smsNotificationProvider;

    public NotificationService(NotificationRepository notificationRepository, EmailNotificationProvider emailNotificationProvider, SMSNotificationProvider smsNotificationProvider) {
        this.notificationRepository = notificationRepository;
        this.emailNotificationProvider = emailNotificationProvider;
        this.smsNotificationProvider = smsNotificationProvider;
    }

    public void sendNotification(String message, String recipient, String channel) {
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setMessage(message);
        notification.setRecipient(recipient);
        notification.setChannel(channel);
        notification.setTimestamp(new Timestamp(System.currentTimeMillis()));
        notificationRepository.save(notification);

        switch (channel.toLowerCase()) {
            case "email":
                emailNotificationProvider.sendEmail(recipient, message);
                break;
            case "sms":
                smsNotificationProvider.sendSMS(recipient, message);
                break;
            default:
                throw new IllegalArgumentException("Unknown notification channel: " + channel);
        }
    }

    //TODO: AlertAdmin method receive message and level of alert and send notification to admin
    public static void alertAdmin(String message, AlertLevel alertLevel, AlertCategory alertCategory) {
        // Logic to alert the admin
    }
}