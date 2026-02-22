package com.andruf.sez.mapper;

import com.andruf.sez.config.MapperConfig;
import com.andruf.sez.entity.ActionRequestNotification;
import com.andruf.sez.entity.Notification;
import com.andruf.sez.gendto.ActionRequestResponse;
import com.andruf.sez.gendto.AnnouncementResponse;
import com.andruf.sez.gendto.CreateActionRequestDto;
import com.andruf.sez.gendto.NotificationActionType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = MapperConfig.class,
        uses = {DateTimeMapper.class})
public interface NotificationMapper {

    AnnouncementResponse toAnnouncementResponse(Notification entity);

    List<AnnouncementResponse> toAnnouncementResponseList(List<Notification> entities);

    ActionRequestResponse toActionResponse(ActionRequestNotification entity);

    List<ActionRequestResponse> toActionResponseList(List<ActionRequestNotification> entities);

    @Mapping(target = "recipient.id", source = "recipientId")
    @Mapping(target = "type", constant = "REQUEST")
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "completed", constant = "false")
    ActionRequestNotification toEntity(CreateActionRequestDto dto);

    default NotificationActionType mapActionType(com.andruf.sez.entity.enums.NotificationActionType type) {
        if (type == null) return null;
        return NotificationActionType.valueOf(type.name());
    }
}