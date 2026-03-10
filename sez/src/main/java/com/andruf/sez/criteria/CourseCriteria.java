package com.andruf.sez.criteria;

import com.andruf.sez.entity.Course;
import com.andruf.sez.entity.Enrollment;
import com.andruf.sez.entity.Subject;
import com.andruf.sez.entity.Tutor;
import com.andruf.sez.entity.enums.EnrollmentStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import java.math.BigDecimal;
import java.util.UUID;

public class CourseCriteria extends Criteria<Course> {

    public CourseCriteria() {
        super(Course.class);
    }

    public void filterByCourseName(String courseName) {
        if (courseName != null && !courseName.isBlank()) {
            add((root, query, cb) -> {
                Join<Course, Subject> subjectJoin = root.join("subject");
                return cb.like(cb.lower(subjectJoin.get("name")), "%" + courseName.toLowerCase() + "%");
            });
        }
    }

    public void filterByTutorId(UUID tutorId) {
        if (tutorId != null) {
            add((root, query, cb) -> cb.equal(root.get("tutor").get("id"), tutorId));
        }
    }

    public void filterByTutorName(String tutorName) {
        if (tutorName != null && !tutorName.isBlank()) {
            add((root, query, cb) -> {
                Join<Course, Tutor> tutorJoin = root.join("tutor");
                String pattern = "%" + tutorName.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(tutorJoin.get("name")), pattern),
                        cb.like(cb.lower(tutorJoin.get("surname")), pattern)
                );
            });
        }
    }

    public void filterByMaxRate(BigDecimal maxRate) {
        if (maxRate != null) {
            add((root, query, cb) -> cb.lessThanOrEqualTo(root.get("hourlyRate"), maxRate));
        }
    }

    public void applySorting(String sortKey) {
        if (sortKey == null) return;

        switch (sortKey) {
            case "price_asc" -> orderBy((root, cb) -> cb.asc(root.get("hourlyRate")));
            case "price_desc" -> orderBy((root, cb) -> cb.desc(root.get("hourlyRate")));
        }
    }
    public void filterByEnrollmentStatus(UUID studentId, boolean excludeEnrolled) {
        if (studentId == null) return;

        add((root, query, cb) -> {
            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<Enrollment> enrollmentRoot = subquery.from(Enrollment.class);

            var predicate = cb.equal(enrollmentRoot.get("student").get("id"), studentId);

            if (excludeEnrolled) {
                subquery.select(enrollmentRoot.get("course").get("id")).where(predicate);
                return cb.not(root.get("id").in(subquery));
            } else {
                subquery.select(enrollmentRoot.get("course").get("id"))
                        .where(cb.and(
                                predicate,
                                cb.equal(enrollmentRoot.get("status"), EnrollmentStatus.ACTIVE)
                        ));
                return root.get("id").in(subquery);
            }
        });
    }
}