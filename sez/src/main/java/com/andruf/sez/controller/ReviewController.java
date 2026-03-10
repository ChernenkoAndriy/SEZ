package com.andruf.sez.controller;

import com.andruf.sez.genapi.ReviewsApi;
import com.andruf.sez.gendto.CreateReviewDto;
import com.andruf.sez.gendto.ReviewResponse;
import com.andruf.sez.gendto.UpdateReviewDto;
import com.andruf.sez.security.services.UserDetailsImpl;
import com.andruf.sez.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
@Slf4j
@RestController
@RequiredArgsConstructor
public class ReviewController implements ReviewsApi {

    private final ReviewService reviewService;
    @PreAuthorize("hasRole('STUDENT')")
    @Override
    public ResponseEntity<Void> createReview(CreateReviewDto createReviewDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        reviewService.createReview(userDetails.getId(), createReviewDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @Override
    public ResponseEntity<Void> updateReview(UUID id, UpdateReviewDto updateReviewDto) {
        reviewService.update(id, updateReviewDto);
        log.info("Review has been updated: {}", id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @Override
    public ResponseEntity<Void> deleteReview(UUID id) {
        reviewService.delete(id);
        log.info("Review has been deleted: {}", id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('STUDENT')")
    @Override
    public ResponseEntity<ReviewResponse> getMyReviewForTutor(UUID tutorId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        ReviewResponse response = reviewService.getReviewByStudentAndTutor(userDetails.getId(), tutorId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @Override
    public ResponseEntity<List<ReviewResponse>> getTutorReviews(UUID tutorId, Boolean sortByRatingUp, Integer page, Integer size) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        List<ReviewResponse> response = reviewService.getTutorReviews(tutorId, userDetails.getId(), sortByRatingUp, page, size);
        return ResponseEntity.ok(response);
    }
}