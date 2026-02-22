package com.andruf.sez.validator;

import com.andruf.sez.entity.Lesson;
import com.andruf.sez.exception.BusinessException;
import com.andruf.sez.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LessonValidator {

    private final LessonRepository lessonRepository;

    public void validate(Lesson lesson) {
        LocalDateTime start = lesson.getStartTime();
        LocalDateTime end = lesson.getEndTime();
        UUID lessonId = lesson.getId() != null ? lesson.getId() : UUID.randomUUID();

        if (start == null || end == null) {
            throw new BusinessException("Start and end times are required", "INVALID_TIME_RANGE");
        }

        if (!start.isBefore(end)) {
            throw new BusinessException("Lesson end time must be after start time", "INVALID_TIME_RANGE");
        }

        if (start.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Lesson cannot be scheduled in the past", "PAST_TIME_NOT_ALLOWED");
        }
        UUID currentId = lesson.getId() != null ? lesson.getId() : UUID.fromString("00000000-0000-0000-0000-000000000000");
        UUID studentId = lesson.getEnrollment().getStudent().getId();
        UUID tutorId = lesson.getEnrollment().getCourse().getTutor().getId();
        List<Lesson> overlaps = lessonRepository.findOverlappingLessons(
                studentId,
                tutorId,
                start,
                end,
                currentId
        );
        if (!overlaps.isEmpty()) {
            Lesson conflict = overlaps.getFirst();
            String message = String.format(
                    "Time conflict: student or tutor already has a lesson from %s to %s",
                    conflict.getStartTime(), conflict.getEndTime()
            );
            throw new BusinessException(message, "SCHEDULE_CONFLICT");
        }
    }
}