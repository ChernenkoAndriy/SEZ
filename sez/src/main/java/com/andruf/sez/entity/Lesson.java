package com.andruf.sez.entity;

import com.andruf.sez.entity.enums.LessonStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lessons")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class Lesson extends BaseEntity<UUID> {
    @OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull(message = "Enrollment reference is required")
    private Enrollment enrollment;

    @NotNull(message = "Start time is required")
    @Future(message = "Lesson cannot be scheduled in the past")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private LessonStatus status;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price cannot be negative")
    private java.math.BigDecimal price;

    @Size(max = 500, message = "Video call URL is too long")
    @Column(name = "video_call_url", length = 500)
    private String videoCallUrl;
}