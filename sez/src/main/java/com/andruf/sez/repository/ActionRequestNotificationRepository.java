package com.andruf.sez.repository;

import com.andruf.sez.entity.ActionRequestNotification;
import com.andruf.sez.entity.enums.NotificationActionType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface ActionRequestNotificationRepository extends IRepository<ActionRequestNotification, UUID> {
    @EntityGraph(attributePaths = {"metadataList"})
    Optional<ActionRequestNotification> findByActionTypeAndIsReadFalseAndCompletedFalseAndRelatedEntityIdAndRecipientId(
            NotificationActionType actionType,
            UUID relatedEntityId,
            UUID recipientId
    );
    @EntityGraph(attributePaths = {"metadataList"})
    Optional<ActionRequestNotification> findByActionTypeAndIsReadFalseAndCompletedFalseAndRelatedEntityId(
            NotificationActionType actionType,
            UUID relatedEntityId
    );
}
