package com.amanatpay.onramp.scheduler;

import com.amanatpay.onramp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationScheduler {

    private final NotificationService notificationService;

    public NotificationScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(fixedRate = 60000) // Check every 60 seconds
    public void checkForImportantEvents() {
        // Logic to check for important events
        boolean importantEventOccurred = checkImportantEvent();

        if (importantEventOccurred) {
            notificationService.sendNotification("Important event occurred!", "admin@example.com", "email");
        }
    }

    private boolean checkImportantEvent() {
        // Implement the logic to check for important events
        return false; // Placeholder
    }
}