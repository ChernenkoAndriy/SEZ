package com.andruf.sez.criteria;

import com.andruf.sez.entity.Notification;
import com.andruf.sez.entity.enums.NotificationType;
import java.util.UUID;

public class NotificationCriteria extends Criteria<Notification> {

    public NotificationCriteria() {
        super(Notification.class);
    }
    public void sortByPriority() {
        orderBy((root, cb) -> cb.desc(root.get("createdAt")));
    }

    public void filterByUserId(UUID userId) {
        if (userId != null) {
            add((root, query, cb) -> cb.equal(root.get("recipient").get("id"), userId));
        }
    }
    public void filterByType(NotificationType type) {
        if (type != null) {
            add((root, query, cb) -> cb.equal(root.get("type"), type));
        }
    }
    public void filterByReadStatus(Boolean isRead) {
        if (isRead != null) {
            add((root, query, cb) -> cb.equal(root.get("isRead"), isRead));
        }
    }
    public void sortByCreatedAtDesc() {
        orderBy((root, cb) -> cb.desc(root.get("createdAt")));
    }
}