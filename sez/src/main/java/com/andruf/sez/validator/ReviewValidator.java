package com.andruf.sez.validator;

import com.andruf.sez.entity.Review;
import com.andruf.sez.exception.BusinessException;
import com.andruf.sez.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewValidator {

    private final ReviewRepository reviewRepository;

    public void validate(Review review) {
        if (review.getStudent() == null || review.getTutor() == null) {
            throw new BusinessException("Student and Tutor references are mandatory for a review", "INVALID_REVIEW_DATA");
        }

        boolean alreadyExists = reviewRepository.existsByStudentAndTutor(
                review.getStudent(),
                review.getTutor()
        );

        if (alreadyExists) {
            throw new BusinessException(
                    String.format("Student %s has already submitted a review for tutor %s",
                            review.getStudent().getId(), review.getTutor().getId()),
                    "DUPLICATE_REVIEW"
            );
        }
    }
}