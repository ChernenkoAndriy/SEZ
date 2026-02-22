package com.andruf.sez.repository;

import com.andruf.sez.entity.Assignment;
import com.andruf.sez.entity.Lesson;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssignmentRepository extends IRepository<Assignment, UUID> {
    boolean existsByLesson(@NotNull(message = "Lesson reference is required") Lesson lesson);
        Optional<Assignment> findByLessonId(UUID lessonId);
}
