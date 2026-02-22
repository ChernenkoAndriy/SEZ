package com.andruf.sez.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "subjects")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class Subject extends BaseEntity<UUID> {

    @NotBlank(message = "Subject name is required")
    @Size(min = 2, max = 100, message = "Subject name must be between 2 and 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String name;
}