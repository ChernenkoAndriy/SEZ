package com.andruf.sez.controller;

import com.andruf.sez.genapi.StudentsApi;
import com.andruf.sez.gendto.StudentResponse;
import com.andruf.sez.gendto.UpdateStudentDto;
import com.andruf.sez.security.services.UserDetailsImpl;
import com.andruf.sez.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StudentController implements StudentsApi {

    private final StudentService studentService;

    @Override
    public ResponseEntity<List<StudentResponse>> getStudents(
            String name,
            String courseName,
            Integer page,
            Integer size) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        List<StudentResponse> students = studentService.findByCriteria(
                name,
                courseName,
                userDetails.getId(),
                page,
                size
        );
        return ResponseEntity.ok(students);
    }

    @Override
    public ResponseEntity<List<StudentResponse>> getStudentsByCourseId(UUID courseId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        UUID tutorId = userDetails.getId();
        return ResponseEntity.ok(studentService.getByCourseId(courseId, tutorId));
    }

    @Override
    public ResponseEntity<StudentResponse> getStudentById(UUID id) {
        StudentResponse student = studentService.getResponseById(id);
        return ResponseEntity.ok(student);
    }

    @Override
    public ResponseEntity<Void> updateStudent(UUID id, UpdateStudentDto updateStudentDto) {
        studentService.update(id, updateStudentDto);
        return ResponseEntity.ok().build();
    }
}