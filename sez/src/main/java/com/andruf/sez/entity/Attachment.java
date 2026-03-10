package com.andruf.sez.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;

import java.util.UUID;

@Entity
@Table(name = "attachments")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class Attachment extends BaseEntity<UUID> {

    private String fileName;
    private String fileType;
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    @OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Assignment assignment;
}