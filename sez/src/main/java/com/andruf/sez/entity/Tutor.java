package com.andruf.sez.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tutors")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class Tutor extends User {
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Size(min = 0, max = 1000, message = "Bio must be between 0 and 2000 characters")
    @Column(columnDefinition = "TEXT")
    private String bio;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @NotNull(message = "Rating is required")
    @Column(nullable = false)
    private Double rating;
}