package com.andruf.sez.service;

import com.andruf.sez.criteria.ReviewCriteria;
import com.andruf.sez.entity.Review;
import com.andruf.sez.exception.EntityNotFoundException;
import com.andruf.sez.gendto.CreateReviewDto;
import com.andruf.sez.gendto.ReviewResponse;
import com.andruf.sez.gendto.UpdateReviewDto;
import com.andruf.sez.repository.ReviewRepository;
import com.andruf.sez.repository.StudentRepository;
import com.andruf.sez.repository.TutorRepository;
import com.andruf.sez.validator.ReviewValidator;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
public class ReviewService extends BaseCRUDService<Review, CreateReviewDto, UpdateReviewDto, ReviewResponse, UUID> {
    @Setter(onMethod_ = @Autowired)
    private ReviewValidator reviewValidator;
    @Setter(onMethod_ = @Autowired)
    private ReviewRepository reviewRepository;
    @Setter(onMethod_ = @Autowired)
    private TutorRepository tutorRepository;
    @Setter(onMethod_ = @Autowired)
    private StudentRepository studentRepository;

    @Transactional
    public void createReview(UUID studentId, CreateReviewDto dto) {
        Review review = mapper.toEntity(dto);
        review.setStudent(studentRepository.getReferenceById(studentId));
        review.setTutor(tutorRepository.getReferenceById(dto.getTutorId()));
        review.setCreatedAt(LocalDateTime.now());
        reviewValidator.validate(review);
        reviewRepository.save(review);
    }

    @Transactional
    public ReviewResponse getReviewByStudentAndTutor(UUID studentId, UUID tutorId) {
        return mapper.toResponse(reviewRepository.findByTutorIdAndStudentId(tutorId, studentId)
                .orElseThrow(() -> new EntityNotFoundException ("No review found for student " + studentId + " and tutor " + tutorId)));

    }

    @Transactional
    public List<ReviewResponse> getTutorReviews(UUID tutorId, UUID studentId, Boolean sortByRatingUp, Integer page, Integer size) {
        ReviewCriteria criteria = new ReviewCriteria();
        criteria.filterByTutorId(tutorId);
        criteria.filterExceptStudentId(studentId);
        if(sortByRatingUp != null && !sortByRatingUp) {
            criteria.sortByOldest();
        } else if(sortByRatingUp != null) {
            criteria.sortByNewest();
        }
        criteria.sortByNewest();
        criteria.setPagination(page != null ? page : 0, size != null ? size : 20);
        return mapper.toResponseList(getList(criteria));
    }

}