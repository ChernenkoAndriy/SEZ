package com.andruf.sez.controller;

import com.andruf.sez.genapi.SubjectsApi;
import com.andruf.sez.gendto.CreateSubjectDto;
import com.andruf.sez.gendto.SubjectResponse;
import com.andruf.sez.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SubjectController implements SubjectsApi {

    private final SubjectService subjectService;

    @Override
    public ResponseEntity<List<SubjectResponse>> getSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @Override
    public ResponseEntity<Void> createSubject(CreateSubjectDto createSubjectDto) {
        subjectService.create(createSubjectDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> deleteSubject(UUID id) {
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}