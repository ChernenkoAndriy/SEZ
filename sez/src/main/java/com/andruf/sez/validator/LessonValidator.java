package com.andruf.sez.validator;

import com.andruf.sez.entity.Lesson;
import com.andruf.sez.exception.BusinessException;
import com.andruf.sez.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LessonValidator {

    private final LessonRepository lessonRepository;

    public void validate(Lesson lesson) {
        OffsetDateTime start = lesson.getStartTime();
        OffsetDateTime end = lesson.getEndTime();

        if (start == null || end == null) {
            throw new BusinessException("Start and end times are required", "INVALID_TIME_RANGE");
        }

        if (!start.isBefore(end)) {
            throw new BusinessException("Lesson end time must be after start time", "INVALID_TIME_RANGE");
        }

        if (!start.toLocalDate().isEqual(end.toLocalDate())) {
            throw new BusinessException("Lesson must start and end on the same day", "INVALID_DURATION");
        }

        if (start.isBefore(OffsetDateTime.now())) {
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

//        if (!overlaps.isEmpty()) {
//            Lesson conflict = overlaps.getFirst();
//            String message = String.format(
//                    "Time conflict: student or tutor already has a lesson from %s to %s",
//                    conflict.getStartTime(), conflict.getEndTime()
//            );
//            throw new BusinessException(message, "SCHEDULE_CONFLICT");
//        }
    }
}