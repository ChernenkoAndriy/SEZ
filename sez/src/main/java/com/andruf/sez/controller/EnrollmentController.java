package com.andruf.sez.controller;

import com.andruf.sez.genapi.EnrollmentsApi;
import com.andruf.sez.gendto.CreateEnrollmentDto;
import com.andruf.sez.gendto.EnrollmentDetailsResponse;
import com.andruf.sez.gendto.EnrollmentResponse;
import com.andruf.sez.gendto.UpdateEnrollmentDto;
import com.andruf.sez.security.services.UserDetailsImpl;
import com.andruf.sez.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class EnrollmentController implements EnrollmentsApi {

    private final EnrollmentService enrollmentService;

    @Override
    public ResponseEntity<EnrollmentDetailsResponse> getEnrollmentDetails(UUID id) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentDetails(id));
    }

    @Override
    public ResponseEntity<List<EnrollmentResponse>> getEnrollments(
            String studentName,
            String tutorName,
            String courseName,
            Integer page,
            Integer size) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<EnrollmentResponse> response = enrollmentService.getEnrollments(
                userDetails,
                studentName,
                tutorName,
                courseName,
                page,
                size
        );

        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<List<EnrollmentDetailsResponse>> getMyActiveEnrollments(String studentName, String courseName) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<EnrollmentDetailsResponse> response = enrollmentService.getMyActiveEnrollments(
                userDetails,
                studentName,
                courseName
        );

        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<Void> requestEnrollment(CreateEnrollmentDto createEnrollmentDto) {
        enrollmentService.create(createEnrollmentDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<Void> updateEnrollmentStatus(UUID id, UpdateEnrollmentDto updateEnrollmentDto) {
        enrollmentService.update(id, updateEnrollmentDto);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<Void> terminateEnrollment(UUID id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        enrollmentService.terminateAndCleanup(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> terminateEnrollmentByCourse(UUID courseId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        enrollmentService.terminateEnrollment(courseId, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}