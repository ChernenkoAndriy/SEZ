package com.andruf.sez.repository;

import com.andruf.sez.entity.Review;
import com.andruf.sez.entity.Student;
import com.andruf.sez.entity.Tutor;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends IRepository<Review, UUID> {
    Optional<Review> findByTutorIdAndStudentId(UUID tutorId, UUID studentId);

    boolean existsByStudentAndTutor(@NotNull(message = "Student reference is required") Student student, @NotNull(message = "Tutor reference is required") Tutor tutor);
}
