package com.andruf.sez.service;

import com.andruf.sez.entity.Subject;
import com.andruf.sez.gendto.CreateSubjectDto;
import com.andruf.sez.gendto.SubjectResponse;
import com.andruf.sez.repository.SubjectRepository;
import com.andruf.sez.validator.SubjectValidator;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    @Autowired
    private SubjectValidator subjectValidator;
    @Autowired
    private SubjectRepository repository;

    @Transactional
    public List<SubjectResponse> getAllSubjects() {
        List<Subject> subjects = repository.findAll();
        return subjects.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SubjectResponse mapToResponse(Subject subject) {
        if (subject == null) {
            return null;
        }

        SubjectResponse response = new SubjectResponse();
        response.setId(subject.getId());
        response.setName(subject.getName());

        return response;
    }

    @Transactional
    public void create(@NonNull CreateSubjectDto createSubjectDto) {
        Subject subject = new Subject();
        subject.setName(createSubjectDto.getName());
        subjectValidator.validate(subject);
        repository.save(subject);
    }

    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}