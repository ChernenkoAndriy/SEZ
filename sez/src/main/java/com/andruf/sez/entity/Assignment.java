package com.andruf.sez.entity;

import com.andruf.sez.entity.enums.AssignmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Assignment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String fileUrl;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    private Integer grade;

    @Column(columnDefinition = "TEXT")
    private String feedback;
}