package com.andruf.sez.repository;

import com.andruf.sez.entity.Course;
import com.andruf.sez.entity.Enrollment;
import com.andruf.sez.entity.Student;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends IRepository<Enrollment, UUID> {
    @Query("SELECT e FROM Enrollment e JOIN FETCH e.student JOIN FETCH e.course WHERE e.id = :id")
    Optional<Enrollment> findByIdWithDetails(@Param("id") UUID id);

    boolean existsByStudentAndCourse(@NotNull(message = "Student reference is required") Student student, @NotNull(message = "Course reference is required") Course course);

    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);
}
