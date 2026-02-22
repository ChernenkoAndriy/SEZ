package com.andruf.sez.criteria;

import com.andruf.sez.entity.Lesson;
import com.andruf.sez.entity.enums.LessonStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class LessonCriteria extends Criteria<Lesson> {

    public LessonCriteria() {
        super(Lesson.class);
    }

    public void filterByStartTimeAfter(LocalDateTime from) {
        if (from != null) {
            add((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startTime"), from));
        }
    }

    public void filterByEndTimeBefore(LocalDateTime to) {
        if (to != null) {
            add((root, query, cb) -> cb.lessThanOrEqualTo(root.get("endTime"), to));
        }
    }

    public void filterByStatus(LessonStatus status) {
        if (status != null) {
            add((root, query, cb) -> cb.equal(root.get("status"), status));
        }
    }

    public void filterByUserId(UUID userId, com.andruf.sez.entity.enums.UserRole role) {
        if (userId != null && role != null) {
            if (role == com.andruf.sez.entity.enums.UserRole.STUDENT) {
                add((root, query, cb) -> cb.equal(root.get("enrollment").get("student").get("id"), userId));
            } else if (role == com.andruf.sez.entity.enums.UserRole.TUTOR) {
                add((root, query, cb) -> cb.equal(root.get("enrollment").get("course").get("tutor").get("id"), userId));
            }
        }
    }

    public void sortByStartTime(boolean ascending) {
        if (ascending) {
            orderBy((root, cb) -> cb.asc(root.get("startTime")));
        } else {
            orderBy((root, cb) -> cb.desc(root.get("startTime")));
        }
    }
}