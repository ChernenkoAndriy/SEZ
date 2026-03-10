package com.andruf.sez.scheduler;

import com.andruf.sez.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationCleanupScheduler {

    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupAnnouncements() {
        log.info("Starting daily cleanup of read notifications older than 3 days...");
        try {
            notificationService.deleteOldReadNotifications();
            log.info("Notification cleanup finished successfully.");
        } catch (Exception e) {
            log.error("Failed to cleanup old notifications", e);
        }
    }

    public void cleanupRequests() {
        log.info("Starting daily cleanup of read request notifications older than 3 days...");
        try {
            notificationService.deleteOldReadRequestNotifications();
            log.info("Request notification cleanup finished successfully.");
        } catch (Exception e) {
            log.error("Failed to cleanup old request notifications", e);
        }
    }
}