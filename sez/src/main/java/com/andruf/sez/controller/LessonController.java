package com.andruf.sez.controller;

import com.andruf.sez.genapi.LessonsApi;
import com.andruf.sez.gendto.CreateLessonDto;
import com.andruf.sez.gendto.LessonDetailsResponse;
import com.andruf.sez.gendto.LessonResponse;
import com.andruf.sez.gendto.UpdateLessonDto;
import com.andruf.sez.security.services.UserDetailsImpl;
import com.andruf.sez.service.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
@Slf4j
@RestController
@RequiredArgsConstructor
public class LessonController implements LessonsApi {

    private final LessonService lessonService;

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> deleteLesson(UUID id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        lessonService.delete(id, userDetails);
        log.info("Lesson has been deleted: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<LessonDetailsResponse> getLessonDetails(UUID id) {
        ResponseEntity<LessonDetailsResponse> response = ResponseEntity.ok(lessonService.getLessonDetails(id));
        return response;
    }

    @Override
    public ResponseEntity<List<LessonResponse>> getMyLessons(OffsetDateTime from, OffsetDateTime to, String status) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<LessonResponse> lessons = lessonService.getMyLessons(
                userDetails,
                from,
                to,
                status
        );

        return ResponseEntity.ok(lessons);
    }

    @PreAuthorize("hasRole('TUTOR')")
    @Override
    public ResponseEntity<Void> scheduleLesson(CreateLessonDto createLessonDto) {
        lessonService.create(createLessonDto);
        log.info("Lesson has been created: {}", createLessonDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('TUTOR')")
    @Override
    public ResponseEntity<Void> updateLesson(UUID id, UpdateLessonDto updateLessonDto) {
        lessonService.update(id, updateLessonDto);
        log.info("Lesson has been updated: {}", updateLessonDto);
        return ResponseEntity.ok().build();
    }

}