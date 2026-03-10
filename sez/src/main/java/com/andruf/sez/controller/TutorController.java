package com.andruf.sez.controller;

import com.andruf.sez.genapi.TutorsApi;
import com.andruf.sez.gendto.TutorResponse;
import com.andruf.sez.gendto.UpdateTutorDto;
import com.andruf.sez.service.TutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class TutorController implements TutorsApi {

    private final TutorService tutorService;

    @Override
    public ResponseEntity<List<TutorResponse>> getTutors() {
        return ResponseEntity.ok(tutorService.getAllTutorResponses());
    }

    @Override
    public ResponseEntity<TutorResponse> getTutorById(UUID id) {
        return ResponseEntity.ok(tutorService.getTutorResponseById(id));
    }

    @PreAuthorize("hasRole('TUTOR')")
    @Override
    public ResponseEntity<Void> updateTutor(UUID id, UpdateTutorDto updateTutorDto) {
        tutorService.update(id, updateTutorDto);
        return ResponseEntity.ok().build();
    }
}