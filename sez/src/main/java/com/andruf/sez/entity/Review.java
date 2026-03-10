package com.andruf.sez.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class Review extends BaseEntity<UUID> {
    @NotNull(message = "Tutor reference is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Tutor tutor;

    @NotNull(message = "Student reference is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Student student;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @NotNull(message = "Rating is required")
    @Column(nullable = false)
    private Integer rating;

    @NotBlank(message = "Comment cannot be empty")
    @Size(min = 10, max = 500, message = "Comment must be between 10 and 500 characters")
    @Column(nullable = false, length = 500)
    private String comment;

}