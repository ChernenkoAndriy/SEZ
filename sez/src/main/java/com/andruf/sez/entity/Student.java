package com.andruf.sez.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "students")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class Student extends User {
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollments;
}