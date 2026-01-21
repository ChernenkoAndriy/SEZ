package com.andruf.sez.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tutors")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Tutor extends User {
}