package com.andruf.sez.criteria;

import com.andruf.sez.entity.Review;
import java.util.UUID;

public class ReviewCriteria extends Criteria<Review> {

    public ReviewCriteria() {
        super(Review.class);
    }

    public void filterByTutorId(UUID tutorId) {
        if (tutorId != null) {
            add((root, query, cb) -> cb.equal(root.get("tutor").get("id"), tutorId));
        }
    }

    public void filterByStudentId(UUID studentId) {
        if (studentId != null) {
            add((root, query, cb) -> cb.equal(root.get("student").get("id"), studentId));
        }
    }

    public void sortByNewest() {

        orderBy((root, cb) -> cb.desc(root.get("createdAt")));
    }

    public void sortByOldest() {
        orderBy((root, cb) -> cb.asc(root.get("createdAt")));
    }

    public void filterExceptStudentId(UUID studentId) {
        if (studentId != null) {
            add((root, query, cb) -> cb.notEqual(root.get("student").get("id"), studentId));
        }
    }
}