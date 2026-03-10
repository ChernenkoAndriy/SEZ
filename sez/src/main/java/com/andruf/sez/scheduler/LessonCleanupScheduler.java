package com.andruf.sez.scheduler;

import com.andruf.sez.service.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LessonCleanupScheduler {

    private final LessonService lessonService;

    @Scheduled(cron = "0 0 3 * * MON")
    public void deleteOldLessonsWeekly() {
        log.info("Starting weekly cleanup of old lessons...");
        try {
            lessonService.cleanupOldLessons();
            log.info("Old lessons cleanup finished successfully.");
        } catch (Exception e) {
            log.error("Error during old lessons cleanup: {}", e.getMessage());
        }
    }
}