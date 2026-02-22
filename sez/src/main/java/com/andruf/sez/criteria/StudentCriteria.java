package com.andruf.sez.criteria;

import com.andruf.sez.entity.Student;
import com.andruf.sez.entity.Enrollment;
import com.andruf.sez.entity.Course;
import com.andruf.sez.entity.Subject;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.util.UUID;

public class StudentCriteria extends Criteria<Student> {

    public StudentCriteria() {
        super(Student.class);
    }

    public void filterByNameOrSurname(String searchTerm) {
        if (searchTerm != null && !searchTerm.isBlank()) {
            add((root, query, cb) -> {
                String pattern = "%" + searchTerm.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("surname")), pattern)
                );
            });
        }
    }

    public void filterBySubjectName(String subjectName) {
        if (subjectName != null && !subjectName.isBlank()) {
            add((root, query, cb) -> {
                Join<Student, Enrollment> enrollmentJoin = root.join("enrollments", JoinType.INNER);
                Join<Enrollment, Course> courseJoin = enrollmentJoin.join("course", JoinType.INNER);
                Join<Course, Subject> subjectJoin = courseJoin.join("subject", JoinType.INNER);

                return cb.like(cb.lower(subjectJoin.get("name")), "%" + subjectName.toLowerCase() + "%");
            });
        }
    }

    public void filterByTutorId(UUID tutorId) {
        if (tutorId != null) {
            add((root, query, cb) -> {
                Join<Student, Enrollment> enrollmentJoin = root.join("enrollments", JoinType.INNER);
                return cb.equal(enrollmentJoin.get("course").get("tutor").get("id"), tutorId);
            });
        }
    }

    public void filterByCourseId(UUID id) {
        if (id != null) {
            add((root, query, cb) -> {
                Join<Student, Enrollment> enrollmentJoin = root.join("enrollments", JoinType.INNER);
                Join<Enrollment, Course> courseJoin = enrollmentJoin.join("course", JoinType.INNER);
                return cb.equal((courseJoin.get("id")), id);
            });
        }
    }
}