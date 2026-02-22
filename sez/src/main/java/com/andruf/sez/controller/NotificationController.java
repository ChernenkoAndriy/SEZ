package com.andruf.sez.controller;

import com.andruf.sez.genapi.NotificationsApi;
import com.andruf.sez.gendto.ActionRequestResponse;
import com.andruf.sez.gendto.AnnouncementResponse;
import com.andruf.sez.gendto.CreateActionRequestDto;
import com.andruf.sez.gendto.NotificationHandleDto;
import com.andruf.sez.security.services.UserDetailsImpl;
import com.andruf.sez.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationsApi {

    private final NotificationService notificationService;

    @Override
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncements(Integer page, Integer size) {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(notificationService.getAnnouncementsForUser(userId, page, size));
    }

    @Override
    public ResponseEntity<List<ActionRequestResponse>> getActionRequests(Integer page, Integer size) {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(notificationService.getActionRequestsForUser(userId, page, size));
    }

    @Override
    public ResponseEntity<Void> handleNotificationAction(UUID id, NotificationHandleDto notificationHandleDto) {
        notificationService.handleAction(id, notificationHandleDto);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> markAsRead(UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> createActionRequest(CreateActionRequestDto createActionRequestDto) {
        notificationService.createActionRequest(createActionRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> deleteNotification(UUID id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Допоміжний метод для отримання ID поточного користувача
    private UUID getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userDetails.getId();
    }
}