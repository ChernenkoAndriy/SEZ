package com.andruf.sez.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "courses")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
public class Course extends BaseEntity<UUID> {

    @NotNull(message = "Tutor is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @NotNull(message = "Subject is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @NotNull(message = "Hourly rate cannot be null")
    @DecimalMin(value = "1.0", message = "Hourly rate must be at least 1.0")
    @Column(nullable = false)
    private BigDecimal hourlyRate;

    @Size(max = 1000, min = 2, message = "Description exceeds maximum length of 1000 characters")
    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Description cannot be null")
    private String description;
}