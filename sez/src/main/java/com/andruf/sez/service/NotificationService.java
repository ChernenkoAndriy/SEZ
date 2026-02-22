package com.andruf.sez.service;

import com.andruf.sez.criteria.CriteriaRepository;
import com.andruf.sez.criteria.NotificationCriteria;
import com.andruf.sez.entity.ActionRequestNotification;
import com.andruf.sez.entity.Notification;
import com.andruf.sez.gendto.ActionRequestResponse;
import com.andruf.sez.gendto.AnnouncementResponse;
import com.andruf.sez.gendto.CreateActionRequestDto;
import com.andruf.sez.gendto.NotificationHandleDto;
import com.andruf.sez.mapper.NotificationMapper;
import com.andruf.sez.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService{

    private final NotificationRepository notificationRepository;
    private final ActionRequestNotificationService actionRequestService;
    @Autowired
    private CriteriaRepository criteriaRepository;
    @Autowired
    private NotificationMapper mapper;

    public NotificationService(
            NotificationRepository notificationRepository,
            ActionRequestNotificationService actionRequestService) {
        this.notificationRepository = notificationRepository;
        this.actionRequestService = actionRequestService;
    }

    @Transactional
    public void handleAction(UUID id, NotificationHandleDto handleDto) {
        actionRequestService.processAction(id, handleDto);
        markAsRead(id);
    }

    @Transactional
    public void markAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id).get();
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void delete(UUID id) {
        notificationRepository.deleteById(id);
    }

    @Transactional
    public void createActionRequest(CreateActionRequestDto dto) {
        ActionRequestNotification notification = mapper.toEntity(dto);
        notificationRepository.save(notification);
    }

    public List<AnnouncementResponse> getAnnouncementsForUser(UUID userId, Integer page, Integer size) {
        NotificationCriteria criteria = new NotificationCriteria();
        criteria.filterByUserId(userId);
        criteria.filterByType(com.andruf.sez.entity.enums.NotificationType.ANNOUNCEMENT);
        criteria.filterByReadStatus(false);
        criteria.sortByPriority();
        criteria.sortByCreatedAtDesc();
        criteria.setPagination(page != null ? page : 0, size != null ? size : 20);
        List<Notification> notifications = criteriaRepository.find(criteria);
        return mapper.toAnnouncementResponseList(notifications);
    }

    public List<ActionRequestResponse> getActionRequestsForUser(UUID userId, Integer page, Integer size) {
        NotificationCriteria criteria = new NotificationCriteria();
        criteria.filterByUserId(userId);
        criteria.filterByReadStatus(false);
        criteria.filterByType(com.andruf.sez.entity.enums.NotificationType.REQUEST);
        criteria.sortByPriority();
        criteria.setPagination(page != null ? page : 0, size != null ? size : 20);
        List<Notification> notifications = criteriaRepository.find(criteria);
        List<ActionRequestNotification> actionRequests = notifications.stream()
                .filter(ActionRequestNotification.class::isInstance)
                .map(ActionRequestNotification.class::cast)
                .toList();
        return mapper.toActionResponseList(actionRequests);
    }
}