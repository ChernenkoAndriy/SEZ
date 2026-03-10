package com.andruf.sez.entity;

import com.andruf.sez.entity.enums.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("ANNOUNCEMENT")
public class Notification extends BaseEntity<UUID> {

    @NotNull(message = "Recipient is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    @OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private User recipient;

    @NotBlank(message = "Message content is required")
    @Size(max = 1000, message = "Message is too long")
    @Column(nullable = false, length = 1000)
    private String message;

    @Builder.Default
    private boolean isRead = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, insertable = false, updatable = false)
    protected NotificationType type;
}