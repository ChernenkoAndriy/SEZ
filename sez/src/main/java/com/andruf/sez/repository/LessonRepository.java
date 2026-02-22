package com.andruf.sez.repository;

import com.andruf.sez.entity.Lesson;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonRepository extends IRepository<Lesson, UUID> {
    @Query("SELECT l FROM Lesson l " +
            "JOIN FETCH l.enrollment e " +
            "JOIN FETCH e.student s " +
            "JOIN FETCH e.course c " +
            "JOIN FETCH c.tutor t " +
            "WHERE l.id = :id")
    Optional<Lesson> findByIdWithFullDetails(@Param("id") UUID id);

    @Query("SELECT l FROM Lesson l " +
            "WHERE (l.enrollment.student.id = :studentId OR l.enrollment.course.tutor.id = :tutorId) " +
            "AND l.status = 'PLANNED' " +
            "AND (:currentLessonId IS NULL OR l.id <> :currentLessonId) " + // Додано перевірку на null
            "AND (:start < l.endTime AND :end > l.startTime)")
    List<Lesson> findOverlappingLessons(@Param("studentId") UUID studentId,
                                        @Param("tutorId") UUID tutorId,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end,
                                        @Param("currentLessonId") UUID currentLessonId);
}
