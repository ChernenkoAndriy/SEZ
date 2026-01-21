package com.andruf.sez.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Subject extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;
}