package com.andruf.sez.repository;

import com.andruf.sez.entity.ActionRequestNotification;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ActionRequestNotificationRepository extends IRepository<ActionRequestNotification, UUID> {
}
