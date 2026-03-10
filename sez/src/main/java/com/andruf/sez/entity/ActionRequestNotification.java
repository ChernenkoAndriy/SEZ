package com.andruf.sez.entity;

import com.andruf.sez.entity.enums.NotificationActionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.util.List;
import java.util.UUID;

@Entity
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

    @OneToMany(
            mappedBy = "notification",
            orphanRemoval = true,
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<NotificationMetadata> metadataList;
}