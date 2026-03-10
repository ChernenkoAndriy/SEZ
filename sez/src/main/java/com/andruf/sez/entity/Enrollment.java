package com.andruf.sez.entity;

import com.andruf.sez.entity.enums.EnrollmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import java.util.UUID;

@Entity
@Table(name = "enrollments")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class Enrollment extends BaseEntity<UUID> {
    @NotNull(message = "Course reference is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotNull(message = "Student reference is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    @OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Student student;

    @NotNull(message = "Enrollment status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status;
}