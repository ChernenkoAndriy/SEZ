package com.andruf.sez.validator;

import com.andruf.sez.entity.*;
import com.andruf.sez.entity.enums.NotificationActionType;
import com.andruf.sez.exception.BusinessException;
import com.andruf.sez.repository.ActionRequestNotificationRepository;
import com.andruf.sez.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class NotificationValidator {

    private final ActionRequestNotificationRepository actionRequestNotificationRepository;
    private final LessonRepository lessonRepository;

    public void validateOwnership(Notification notification, User currentUser) {
        if (!notification.getRecipient().getId().equals(currentUser.getId())) {
            throw new BusinessException(
                    "You are not authorized to access this notification",
                    "NOTIFICATION_ACCESS_DENIED"
            );
        }
    }

    public void validateActionProcessing(ActionRequestNotification notification) {
        if (notification.isCompleted()) {
            throw new BusinessException(
                    "This action request has already been processed and cannot be changed",
                    "ACTION_ALREADY_COMPLETED"
            );
        }
        if (notification.getRelatedEntityId() == null) {
            throw new BusinessException(
                    "Notification is missing a reference to the related entity",
                    "INVALID_NOTIFICATION_DATA"
            );
        }
        if (notification.getMetadataList() == null) {
            throw new BusinessException(
                    "Notification is missing a metadata map required for processing the action",
                    "INVALID_NOTIFICATION_DATA"
            );
        }

        switch (notification.getActionType()) {
            case LESSON_CANCEL -> validateLessonCancel(notification);
            case LESSON_RESCHEDULE -> validateLessonReschedule(notification);
            case ENROLLMENT_CONFIRM -> validateEnrollmentConfirm(notification);
        }
    }

    private void validateLessonCancel(ActionRequestNotification notification) {
        actionRequestNotificationRepository
                .findByActionTypeAndIsReadFalseAndCompletedFalseAndRelatedEntityId(
                        NotificationActionType.LESSON_CANCEL,
                        notification.getRelatedEntityId()
                ).ifPresent(existing -> {
                    throw new BusinessException("A cancellation request for this lesson is already pending", "DUPLICATE_ACTION_REQUEST");
                });
    }

    private void validateLessonReschedule(ActionRequestNotification notification) {
        actionRequestNotificationRepository
                .findByActionTypeAndIsReadFalseAndCompletedFalseAndRelatedEntityId(
                        NotificationActionType.LESSON_RESCHEDULE,
                        notification.getRelatedEntityId()
                ).ifPresent(existing -> {
                    if (!existing.getId().equals(notification.getId())) {
                        throw new BusinessException("A rescheduling request for this lesson is already pending", "DUPLICATE_ACTION_REQUEST");
                    }
                });
        List<NotificationMetadata> metadataList = notification.getMetadataList();
        Map<String, String> metadata = new HashMap<>();
        for (NotificationMetadata meta : metadataList) {
            metadata.put(meta.getKey(), meta.getValue());
        }
        OffsetDateTime newStart;
        OffsetDateTime newEnd;

        try {
            newStart = OffsetDateTime.parse(metadata.get("newStartTime"));
            newEnd = OffsetDateTime.parse(metadata.get("newEndTime"));
        } catch (DateTimeParseException | NullPointerException e) {
            throw new BusinessException(
                    "Invalid date format in metadata. Expected ISO-8601 with offset (e.g. 2026-03-01T14:18:00+02:00)",
                    "INVALID_METADATA_FORMAT"
            );
        }

        OffsetDateTime now = OffsetDateTime.now();
        if (newStart.isBefore(now)) {
            throw new BusinessException("New start time cannot be in the past", "INVALID_SCHEDULE_TIME");
        }
        if (!newEnd.isAfter(newStart)) {
            throw new BusinessException("End time must be after start time", "INVALID_SCHEDULE_TIME");
        }

        Lesson lesson = lessonRepository.findById(notification.getRelatedEntityId())
                .orElseThrow(() -> new BusinessException("Lesson not found", "LESSON_NOT_FOUND"));

        if (!newStart.toLocalDate().isEqual(newEnd.toLocalDate())) {
            throw new BusinessException("Rescheduling is only allowed within the same day", "INVALID_RESCHEDULE_DAY");
        }
        UUID studentId = lesson.getEnrollment().getStudent().getId();
        UUID tutorId = lesson.getEnrollment().getCourse().getTutor().getId();
        UUID lessonId = lesson.getEnrollment().getId();
        List<Lesson> existinglessons = lessonRepository.findOverlappingLessons(
                studentId,
                tutorId,
                newStart,
                newEnd,
                lessonId
        );
        if (!existinglessons.isEmpty()) {
            throw new BusinessException("The recipient already has a lesson scheduled at this time", "SCHEDULE_CONFLICT");
        }
    }

    private void validateEnrollmentConfirm(ActionRequestNotification notification) {
        actionRequestNotificationRepository
                .findByActionTypeAndIsReadFalseAndCompletedFalseAndRelatedEntityIdAndRecipientId(
                        NotificationActionType.ENROLLMENT_CONFIRM,
                        notification.getRelatedEntityId(),
                        notification.getRecipient().getId()
                ).ifPresent(existing -> {
                    throw new BusinessException("An enrollment request for this course is already pending", "DUPLICATE_ENROLLMENT_REQUEST");
                });
    }
}