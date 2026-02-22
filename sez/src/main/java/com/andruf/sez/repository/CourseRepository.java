package com.andruf.sez.repository;

import com.andruf.sez.entity.Course;
import com.andruf.sez.entity.Subject;
import com.andruf.sez.entity.Tutor;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;


import java.util.UUID;
@Repository
public interface CourseRepository extends IRepository<Course, UUID> {
    boolean existsByTutorAndSubject(@NotNull(message = "Tutor is required") Tutor tutor, @NotNull(message = "Subject is required") Subject subject);
}
