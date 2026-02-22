package com.andruf.sez.validator;

import com.andruf.sez.entity.Enrollment;
import com.andruf.sez.exception.BusinessException;
import com.andruf.sez.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnrollmentValidator {

    private final EnrollmentRepository enrollmentRepository;

    public void validateCreation(Enrollment enrollment) {
        if (enrollment.getStudent().getId().equals(
                enrollment.getCourse().getTutor().getId())) {
            throw new BusinessException(
                    "Tutors cannot enroll in their own courses",
                    "SELF_ENROLLMENT_NOT_ALLOWED"
            );
        }

        boolean alreadyEnrolled = enrollmentRepository.existsByStudentAndCourse(
                enrollment.getStudent(),
                enrollment.getCourse()
        );

        if (alreadyEnrolled) {
            throw new BusinessException(
                    "Student is already enrolled in this course",
                    "ALREADY_ENROLLED"
            );
        }
    }
}