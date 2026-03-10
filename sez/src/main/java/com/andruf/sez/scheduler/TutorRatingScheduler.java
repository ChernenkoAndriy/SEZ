package com.andruf.sez.scheduler;

import com.andruf.sez.service.TutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TutorRatingScheduler {

    private final TutorService tutorService;

    @Scheduled(cron = "0 0 * * * *")
    public void updateTutorRatings() {
        log.info("Запуск планового перерахунку рейтингу вчителів...");
        try {
            tutorService.recalculateAllRatings();
            log.info("Рейтинги успішно оновлено.");
        } catch (Exception e) {
            log.error("Помилка під час оновлення рейтингів: ", e);
        }
    }
}