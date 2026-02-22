package com.andruf.sez.validator;

import com.andruf.sez.entity.Course;
import com.andruf.sez.exception.BusinessException;
import com.andruf.sez.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseValidator {

    private final CourseRepository courseRepository;

    public void validate(Course course) {
        if(course.getId() == null) {
            boolean exists = courseRepository.existsByTutorAndSubject(
                    course.getTutor(),
                    course.getSubject()
            );

            if (exists) {
                throw new BusinessException(
                        "Tutor already has a course for this subject",
                        "DUPLICATE_COURSE"
                );
            }
        }
    }
}