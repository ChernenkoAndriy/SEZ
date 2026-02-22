package com.andruf.sez.entity;

import com.andruf.sez.entity.enums.NotificationActionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "action_request_notifications")
@DiscriminatorValue("REQUEST")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class ActionRequestNotification extends Notification {

    @NotNull(message = "Action type is required")
    @Enumerated(EnumType.STRING)
    private NotificationActionType actionType;

    @NotNull(message = "Related entity ID is required")
    private UUID relatedEntityId;

    @Builder.Default
    private boolean completed = false;

    @ElementCollection
    @CollectionTable(
            name = "notification_metadata",
            joinColumns = @JoinColumn(name = "notification_id")
    )
    @MapKeyColumn(name = "meta_key")
    @Column(name = "meta_value")
    private Map<String, String> metadata;
}