package com.andruf.sez.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class BaseEntity<ID extends Comparable<ID>> implements Serializable, IGettableById<ID> {

    @Id
    @GeneratedValue
    private ID id;

    @CreationTimestamp
    @Column(updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}