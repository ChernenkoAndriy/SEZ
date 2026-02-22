package com.andruf.sez.repository;

import com.andruf.sez.entity.Notification;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends IRepository<Notification, UUID> {
}
