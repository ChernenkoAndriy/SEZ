package com.andruf.sez.repository;

import com.andruf.sez.entity.Lesson;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
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
            "AND (:currentLessonId IS NULL OR l.id <> :currentLessonId) " +
            "AND (:start < l.endTime AND :end > l.startTime)")
    List<Lesson> findOverlappingLessons(@Param("studentId") UUID studentId,
                                        @Param("tutorId") UUID tutorId,
                                        @Param("start") OffsetDateTime start,
                                        @Param("end") OffsetDateTime end,
                                        @Param("currentLessonId") UUID currentLessonId);
    @Transactional
    @Modifying
    @Query("DELETE FROM Lesson l WHERE l.endTime < :thresholdDate AND l.status = 'FINISHED'")
    void deleteOldFinishedLessons(@Param("thresholdDate") OffsetDateTime thresholdDate);
}
