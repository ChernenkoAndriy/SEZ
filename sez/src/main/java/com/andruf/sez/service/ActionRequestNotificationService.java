package com.andruf.sez.service;

import com.andruf.sez.entity.ActionRequestNotification;
import com.andruf.sez.entity.Enrollment;
import com.andruf.sez.entity.Lesson;
import com.andruf.sez.entity.enums.EnrollmentStatus;
import com.andruf.sez.entity.enums.NotificationActionType;
import com.andruf.sez.exception.BusinessException;
import com.andruf.sez.exception.EntityNotFoundException;
import com.andruf.sez.gendto.NotificationHandleDto;
import com.andruf.sez.repository.ActionRequestNotificationRepository;
import com.andruf.sez.repository.EnrollmentRepository;
import com.andruf.sez.repository.LessonRepository;
import com.andruf.sez.validator.LessonValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActionRequestNotificationService {

    private final ActionRequestNotificationRepository repository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final LessonValidator lessonValidator;

    @Transactional
    public boolean processAction(UUID id, NotificationHandleDto handleDto) {
        ActionRequestNotification notification = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification Not Found: " + id));

        if (notification.isCompleted()) {
            throw new BusinessException("This action was already processed", "ACTION_ALREADY_PROCESSED");
        }

        if (handleDto.getConfirmed() != null && handleDto.getConfirmed()) {
            handleConfirmation(notification);
        } else {
            notification.setCompleted(true);
            return false;
        }
        notification.setCompleted(true);
        repository.save(notification);
        return  true;
    }

    private void handleConfirmation(ActionRequestNotification notification) {
        NotificationActionType actionType = notification.getActionType();
        UUID relatedId = notification.getRelatedEntityId();
        Map<String, String> metadata = notification.getMetadata();

        switch (actionType) {
            case ENROLLMENT_CONFIRM:
                Enrollment enrollment = enrollmentRepository.findById(relatedId)
                        .orElseThrow(() -> new EntityNotFoundException("Enrollment Not Found"));
                enrollment.setStatus(EnrollmentStatus.ACTIVE);
                enrollmentRepository.save(enrollment);
                break;

            case LESSON_RESCHEDULE:
                Lesson lesson = lessonRepository.findById(relatedId)
                        .orElseThrow(() -> new EntityNotFoundException("Lesson Not Found"));
                if (metadata.containsKey("newStartTime")) {
                    lesson.setStartTime(OffsetDateTime.parse(metadata.get("newStartTime")).toLocalDateTime());
                }
                if (metadata.containsKey("newEndTime")) {
                    lesson.setEndTime(OffsetDateTime.parse(metadata.get("newEndTime")).toLocalDateTime());
                }
                lessonValidator.validate(lesson);
                lessonRepository.save(lesson);
                break;

            case LESSON_CANCEL:
                lessonRepository.deleteById(relatedId);
                break;

            default:
                throw new BusinessException("Unsupported action type of notification: " + actionType, "UNSUPPORTED_ACTION");
        }
    }
}