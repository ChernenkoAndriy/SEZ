package com.andruf.sez.event;

import com.andruf.sez.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent extends ApplicationEvent {
    private final User recipient;
    private final String message;

    public NotificationEvent(Object source, User recipient, String message) {
        super(source);
        this.recipient = recipient;
        this.message = message;
    }
}