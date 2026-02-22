package com.andruf.sez.repository;

import com.andruf.sez.entity.Subject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubjectRepository extends IRepository<Subject, UUID> {
    boolean existsByNameIgnoreCase(@NotBlank(message = "Subject name is required") @Size(min = 2, max = 100, message = "Subject name must be between 2 and 100 characters") String name);
}
