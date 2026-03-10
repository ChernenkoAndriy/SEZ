package com.andruf.sez.repository;

import com.andruf.sez.entity.Notification;
import com.andruf.sez.entity.enums.NotificationType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends IRepository<Notification, UUID> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.isRead = true " +
            "AND n.createdAt < :date " +
            "AND n.type = com.andruf.sez.entity.enums.NotificationType.ANNOUNCEMENT")
    void deleteOldReadNotifications(@Param("date") OffsetDateTime date);
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.isRead = true " +
            "AND n.createdAt < :date " +
            "AND n.type = com.andruf.sez.entity.enums.NotificationType.REQUEST")
    void deleteOldReadRequestNotifications(@Param("date") OffsetDateTime date);

    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.id IN (" +
            "SELECT an.id FROM ActionRequestNotification an WHERE an.relatedEntityId IN :lessonIds)")
    void deleteActionRequestsByRelatedEntityIds(@Param("lessonIds") List<UUID> lessonIds);
}