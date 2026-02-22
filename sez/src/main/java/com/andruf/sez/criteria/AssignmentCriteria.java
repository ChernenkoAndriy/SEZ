package com.andruf.sez.criteria;

import com.andruf.sez.entity.Assignment;
import com.andruf.sez.entity.enums.AssignmentStatus;
import java.util.UUID;

public class AssignmentCriteria extends Criteria<Assignment> {

    public AssignmentCriteria() {
        super(Assignment.class);
    }

    public void filterByStudentName(String studentName) {
        if (studentName != null && !studentName.isBlank()) {
            add((root, query, cb) -> {
                var studentJoin = root.join("lesson").join("enrollment").join("student");
                String pattern = "%" + studentName.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(studentJoin.get("name")), pattern),
                        cb.like(cb.lower(studentJoin.get("surname")), pattern)
                );
            });
        }
    }

    public void filterByCourseId(UUID courseId) {
        if (courseId != null) {
            add((root, query, cb) ->
                    cb.equal(root.get("lesson").get("enrollment").get("course").get("id"), courseId)
            );
        }
    }

    public void filterByStatus(AssignmentStatus status) {
        if (status != null) {
            add((root, query, cb) -> cb.equal(root.get("status"), status));
        }
    }

    public void filterByStudentId(UUID studentId) {
        if (studentId != null) {
            add((root, query, cb) ->
                    cb.equal(root.get("lesson").get("enrollment").get("student").get("id"), studentId)
            );
        }
    }

    public void filterByTutorId(UUID tutorId) {
        if (tutorId != null) {
            add((root, query, cb) ->
                    cb.equal(root.get("lesson").get("enrollment").get("course").get("tutor").get("id"), tutorId)
            );
        }
    }

    public void sortByLessonDate(boolean ascending) {
        if (ascending) {
            orderBy((root, cb) -> cb.asc(root.get("lesson").get("startTime")));
        } else {
            orderBy((root, cb) -> cb.desc(root.get("lesson").get("startTime")));
        }
    }

    public void filterByCourseName(String courseName) {
        if (courseName != null && !courseName.isBlank()) {
            add((root, query, cb) -> {
                var courseJoin = root.join("lesson").join("enrollment").join("course").join("subject");
                String pattern = "%" + courseName.toLowerCase() + "%";
                return cb.like(cb.lower(courseJoin.get("name")), pattern);
            });
        }
    }

    public void filterByTutorName(String tutorName) {
        if (tutorName != null && !tutorName.isBlank()) {
            add((root, query, cb) -> {
                var tutorJoin = root.join("lesson").join("enrollment").join("course").join("tutor");
                String pattern = "%" + tutorName.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(tutorJoin.get("name")), pattern),
                        cb.like(cb.lower(tutorJoin.get("surname")), pattern)
                );
            });
        }
    }
}