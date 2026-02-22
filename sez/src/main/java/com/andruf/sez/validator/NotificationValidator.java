package com.andruf.sez.validator;

import com.andruf.sez.entity.ActionRequestNotification;
import com.andruf.sez.entity.Notification;
import com.andruf.sez.entity.User;
import com.andruf.sez.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationValidator {

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
        if (notification.getMetadata() == null) {
            throw new BusinessException(
                    "Notification is missing a metadata map required for processing the action",
                    "INVALID_NOTIFICATION_DATA"
            );
        }
    }
}