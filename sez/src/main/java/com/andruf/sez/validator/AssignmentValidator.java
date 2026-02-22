package com.andruf.sez.validator;

import com.andruf.sez.entity.Assignment;
import com.andruf.sez.entity.enums.AssignmentStatus;
import com.andruf.sez.exception.BusinessException;
import com.andruf.sez.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignmentValidator {
    private final AssignmentRepository assignmentRepository;
    public void validateCompletion(Assignment assignment) {
        if (assignment.getStatus() != AssignmentStatus.PENDING) {
            throw new BusinessException(
                    "Only assignments with status PENDING can be marked as COMPLETED",
                    "INVALID_STATUS_TRANSITION"
            );
        }
    }
    public void validateCreation(Assignment assignment) {
        if (assignment.getLesson() == null) {
            throw new BusinessException("Lesson reference is required", "LESSON_REQUIRED");
        }

        boolean alreadyExists = assignmentRepository.existsByLesson(assignment.getLesson());
        if (alreadyExists) {
            throw new BusinessException(
                    "An assignment already exists for this lesson. Only one assignment per lesson is allowed.",
                    "DUPLICATE_ASSIGNMENT"
            );
        }
    }
    public void validateGrading(Assignment assignment, Integer grade) {
        if (assignment.getStatus() != AssignmentStatus.COMPLETED) {
            throw new BusinessException(
                    "Only completed assignments can be graded",
                    "ASSIGNMENT_NOT_COMPLETED"
            );
        }
        if (grade < 0 || grade > 100) {
            throw new BusinessException("Grade must be between 0 and 100", "INVALID_GRADE");
        }
    }
}