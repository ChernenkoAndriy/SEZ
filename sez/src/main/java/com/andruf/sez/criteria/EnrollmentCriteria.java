package com.andruf.sez.criteria;

import com.andruf.sez.entity.Course;
import com.andruf.sez.entity.Enrollment;
import com.andruf.sez.entity.Subject;
import com.andruf.sez.entity.Tutor;
import com.andruf.sez.entity.Student;
import com.andruf.sez.entity.enums.EnrollmentStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

import java.util.UUID;

public class EnrollmentCriteria extends Criteria<Enrollment> {

    public EnrollmentCriteria() {
        super(Enrollment.class);
    }

    public void filterByStatus(EnrollmentStatus status) {
        if (status != null) {
            add((root, query, cb) -> cb.equal(root.get("status"), status));
        }
    }

    public void filterByTutorName(String tutorName) {
        if (tutorName != null && !tutorName.isBlank()) {
            add((root, query, cb) -> {
                Join<Enrollment, Course> courseJoin = root.join("course", JoinType.LEFT);
                Join<Course, Tutor> tutorJoin = courseJoin.join("tutor", JoinType.LEFT);

                String pattern = "%" + tutorName.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(tutorJoin.get("name")), pattern),
                        cb.like(cb.lower(tutorJoin.get("surname")), pattern)
                );
            });
        }
    }

    public void filterByStudentName(String studentName) {
        if (studentName != null && !studentName.isBlank()) {
            add((root, query, cb) -> {
                Join<Enrollment, Student> studentJoin = root.join("student", JoinType.LEFT);
                String pattern = "%" + studentName.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(studentJoin.get("name")), pattern),
                        cb.like(cb.lower(studentJoin.get("surname")), pattern)
                );
            });
        }
    }

    public void filterBySubjectName(String subjectName) {
        if (subjectName != null && !subjectName.isBlank()) {
            add((root, query, cb) -> {
                Join<Enrollment, Course> courseJoin = root.join("course", JoinType.LEFT);
                Join<Course, Subject> subjectJoin = courseJoin.join("subject", JoinType.LEFT);

                return cb.like(
                        cb.lower(subjectJoin.get("name")),
                        "%" + subjectName.toLowerCase() + "%"
                );
            });
        }
    }

    public void filterByStudentId(UUID studentId) {
        if (studentId != null) {
            add((root, query, cb) -> {
                Join<Enrollment, Student> studentJoin = root.join("student", JoinType.LEFT);
                return cb.equal(studentJoin.get("id"), studentId);
            });
        }
    }

    public void filterByTutorId(UUID tutorId) {
        if (tutorId != null) {
            add((root, query, cb) -> {
                Join<Enrollment, Course> courseJoin = root.join("course", JoinType.LEFT);
                return cb.equal(courseJoin.get("tutor").get("id"), tutorId);
            });
        }
    }
}