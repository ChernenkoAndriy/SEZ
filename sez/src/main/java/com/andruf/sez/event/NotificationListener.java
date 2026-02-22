package com.andruf.sez.event;

import com.andruf.sez.entity.Notification;
import com.andruf.sez.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationRepository repository;

    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        Notification notification = Notification.builder()
                .recipient(event.getRecipient())
                .message(event.getMessage())
                .isRead(false)
                .build();
        repository.save(notification);
    }
}