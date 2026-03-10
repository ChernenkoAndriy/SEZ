package com.andruf.sez.service;

import com.andruf.sez.entity.ActionRequestNotification;
import com.andruf.sez.entity.Enrollment;
import com.andruf.sez.entity.Lesson;
import com.andruf.sez.entity.NotificationMetadata;
import com.andruf.sez.entity.enums.EnrollmentStatus;
import com.andruf.sez.entity.enums.NotificationActionType;
import com.andruf.sez.event.NotificationEvent;
import com.andruf.sez.exception.BusinessException;
import com.andruf.sez.exception.EntityNotFoundException;
import com.andruf.sez.gendto.NotificationHandleDto;
import com.andruf.sez.repository.ActionRequestNotificationRepository;
import com.andruf.sez.repository.EnrollmentRepository;
import com.andruf.sez.repository.LessonRepository;
import com.andruf.sez.validator.LessonValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActionRequestNotificationService {

    private final ActionRequestNotificationRepository repository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final LessonValidator lessonValidator;
    private final ApplicationEventPublisher eventPublisher;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

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
            handleRejection(notification);
            notification.setCompleted(true);
            notification.setRead(true);
            repository.save(notification);
            return false;
        }

        notification.setCompleted(true);
        notification.setRead(true);
        repository.save(notification);
        return true;
    }

    private void handleConfirmation(ActionRequestNotification notification) {
        NotificationActionType actionType = notification.getActionType();
        UUID relatedId = notification.getRelatedEntityId();
        List<NotificationMetadata> metadataList = notification.getMetadataList();

        switch (actionType) {
            case ENROLLMENT_CONFIRM:
                Enrollment enrollment = enrollmentRepository.findById(relatedId)
                        .orElseThrow(() -> new EntityNotFoundException("Enrollment Not Found"));
                enrollment.setStatus(EnrollmentStatus.ACTIVE);
                enrollmentRepository.save(enrollment);

                publishInfo(enrollment.getStudent().getUser(),
                        "Your enrollment has been confirmed for course: " + enrollment.getCourse().getDescription());
                break;

            case LESSON_RESCHEDULE:
                HashMap<String, String> metadata = new HashMap<>();
                for (NotificationMetadata meta : metadataList) {
                    metadata.put(meta.getKey(), meta.getValue());
                }
                Lesson lesson = lessonRepository.findById(relatedId)
                        .orElseThrow(() -> new EntityNotFoundException("Lesson Not Found"));
                if (metadata.containsKey("newStartTime")) {
                    lesson.setStartTime(OffsetDateTime.parse(metadata.get("newStartTime")));
                }
                if (metadata.containsKey("newEndTime")) {
                    lesson.setEndTime(OffsetDateTime.parse(metadata.get("newEndTime")));
                }
                lessonValidator.validate(lesson);
                lessonRepository.save(lesson);

                String timeRange = formatTimeRange(lesson.getStartTime(), lesson.getEndTime());
                publishInfo(lesson.getEnrollment().getStudent().getUser(), "Your lesson has been moved to: " + timeRange);
                publishInfo(lesson.getEnrollment().getCourse().getTutor().getUser(), "Your lesson has been moved to: " + timeRange);
                break;

            case LESSON_CANCEL:
                Lesson lessonToCancel = lessonRepository.findById(relatedId)
                        .orElseThrow(() -> new EntityNotFoundException("Lesson Not Found"));
                String cancelledTime = formatTimeRange(lessonToCancel.getStartTime(), lessonToCancel.getEndTime());
                lessonRepository.deleteById(relatedId);

                publishInfo(lessonToCancel.getEnrollment().getStudent().getUser(), "Your lesson on " + cancelledTime + " has been cancelled");
                publishInfo(lessonToCancel.getEnrollment().getCourse().getTutor().getUser(), "Your lesson on " + cancelledTime + " has been cancelled");
                break;

            default:
                throw new BusinessException("Unsupported action type: " + actionType, "UNSUPPORTED_ACTION");
        }
    }

    private void handleRejection(ActionRequestNotification notification) {
        NotificationActionType actionType = notification.getActionType();
        UUID relatedId = notification.getRelatedEntityId();

        switch (actionType) {
            case ENROLLMENT_CONFIRM:
                Enrollment enrollment = enrollmentRepository.findById(relatedId)
                        .orElseThrow(() -> new EntityNotFoundException("Enrollment Not Found"));
                publishInfo(enrollment.getStudent().getUser(),
                        "Your enrollment request for " + enrollment.getCourse().getDescription() + " was declined.");
                break;

            case LESSON_RESCHEDULE:
                Lesson lesson = lessonRepository.findById(relatedId)
                        .orElseThrow(() -> new EntityNotFoundException("Lesson Not Found"));
                publishInfo(lesson.getEnrollment().getStudent().getUser(), "Reschedule request for lesson on " + formatDateTime(lesson.getStartTime()) + " was declined.");
                publishInfo(lesson.getEnrollment().getCourse().getTutor().getUser(), "Reschedule request for lesson on " + formatDateTime(lesson.getStartTime()) + " was declined.");
                break;

            case LESSON_CANCEL:
                Lesson lessonToCancel = lessonRepository.findById(relatedId)
                        .orElseThrow(() -> new EntityNotFoundException("Lesson Not Found"));
                publishInfo(lessonToCancel.getEnrollment().getStudent().getUser(), "Cancellation request for lesson on " + formatDateTime(lessonToCancel.getStartTime()) + " was declined.");
                break;
        }
    }

    private String formatDateTime(OffsetDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }

    private String formatTimeRange(OffsetDateTime start, OffsetDateTime end) {
        return String.format("%s - %s", start.format(DATE_FORMATTER), end.format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private void publishInfo(com.andruf.sez.entity.User user, String message) {
        eventPublisher.publishEvent(new NotificationEvent(this, user, message));
    }
}