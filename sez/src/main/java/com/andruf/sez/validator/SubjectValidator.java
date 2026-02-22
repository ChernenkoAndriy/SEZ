package com.andruf.sez.validator;

import com.andruf.sez.entity.Subject;
import com.andruf.sez.exception.BusinessException;
import com.andruf.sez.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubjectValidator {

    private final SubjectRepository subjectRepository;

    public void validate(Subject subject) {
        if (subjectRepository.existsByNameIgnoreCase(subject.getName())) {
            throw new BusinessException(
                    "Subject with name '" + subject.getName() + "' already exists",
                    "DUPLICATE_SUBJECT_NAME"
            );
        }
    }
}