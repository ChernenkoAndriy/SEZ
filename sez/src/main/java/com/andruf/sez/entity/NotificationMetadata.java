package com.andruf.sez.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "notification_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationMetadata extends BaseEntity<UUID> {

    @Column(name = "meta_key", nullable = false)
    private String key;

    @Column(name = "meta_value")
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id")
    private ActionRequestNotification notification;
}