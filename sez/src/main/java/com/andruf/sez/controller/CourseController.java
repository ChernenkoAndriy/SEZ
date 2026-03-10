package com.andruf.sez.controller;

import com.andruf.sez.entity.Course;
import com.andruf.sez.genapi.CoursesApi;
import com.andruf.sez.gendto.CourseResponse;
import com.andruf.sez.gendto.CreateCourseDto;
import com.andruf.sez.gendto.UpdateCourseDto;
import com.andruf.sez.mapper.CourseMapper;
import com.andruf.sez.security.services.UserDetailsImpl;
import com.andruf.sez.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Slf4j
@RestController
@RequiredArgsConstructor
public class CourseController implements CoursesApi {

    private final CourseService courseService;


    @PreAuthorize("hasRole('TUTOR')")
    @Override
    public ResponseEntity<Void> createCourse(CreateCourseDto createCourseDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        courseService.create(createCourseDto, userDetails);
        log.info("Created new course by user {}", userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('TUTOR')")
    @Override
    public ResponseEntity<Void> updateCourse(UUID id, UpdateCourseDto updateCourseDto) {
        courseService.update(id, updateCourseDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('TUTOR')")
    @Override
    public ResponseEntity<Void> deleteCourse(UUID id) {
        courseService.delete(id);
        log.info("Deleted course by user {}", SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CourseResponse> getCourseById(UUID id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @Override
    public ResponseEntity<List<CourseResponse>> getMyCourses(String courseName, Integer page, Integer size) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<CourseResponse> response = courseService.getMyCourses(courseName, userDetails, page, size);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<CourseResponse>> searchCourses(
            String tutorName,
            String courseName,
            BigDecimal maxRate,
            Boolean excludeEnrolled,
            List<String> sortBy,
            Integer page,
            Integer size) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        List<CourseResponse> response = courseService.searchCourses(
                tutorName,
                courseName,
                maxRate,
                excludeEnrolled,
                sortBy,
                page,
                size,
                userDetails.getId()
        );

        return ResponseEntity.ok(response);
    }
}