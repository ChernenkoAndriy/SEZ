package com.andruf.sez.controller;

import com.andruf.sez.entity.Attachment;
import com.andruf.sez.exception.BusinessException;
import com.andruf.sez.genapi.AssignmentsApi;
import com.andruf.sez.gendto.*;
import com.andruf.sez.security.services.UserDetailsImpl;
import com.andruf.sez.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
@Slf4j
@RestController
@RequiredArgsConstructor
public class AssignmentController implements AssignmentsApi {

    private final AssignmentService assignmentService;
    @PreAuthorize("hasRole('STUDENT')")
    @Override
    public ResponseEntity<Void> completeAssignment(UUID id) {
        assignmentService.submitSolution(id);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasRole('TUTOR')")
    @Override
    public ResponseEntity<Void> createAssignment(UUID lessonId, CreateAssignmentDto createAssignmentDto) {
        assignmentService.createForLesson(lessonId, createAssignmentDto);
        log.info("Assignment {} created", createAssignmentDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @Override
    public ResponseEntity<Void> uploadAttachment(UUID id, MultipartFile file) {
        try {
            assignmentService.addAttachment(id, file);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IOException e) {
            throw new BusinessException("Could not upload file: " + e.getMessage(), "FILE_UPLOAD_ERROR");
        }
    }

    @Override
    public ResponseEntity<Resource> downloadAttachment(UUID attachmentId) {
        Attachment attachment = assignmentService.getAttachmentMetadata(attachmentId);
        Resource resource = assignmentService.loadAttachmentAsResource(attachment.getFilePath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .body(resource);
    }

    @Override
    public ResponseEntity<AssignmentResponse> getAssignmentByLesson(UUID id) {
        AssignmentResponse response = assignmentService.getAssignmentByLesson(id);
        log.info("Assignment {} found for lesson", id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @Override
    public ResponseEntity<List<AssignmentResponse>> getStudentAssignments(AssignmentStatus status,
                                                                          String courseName,
                                                                          String tutorName,
                                                                          Boolean dateDescending,
                                                                          Integer page,
                                                                          Integer size) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        UUID userId = userDetails.getId();
        List<AssignmentResponse> response = assignmentService.getStudentAssignmentsWithFilters(status,
                courseName,
                tutorName,
                userId,
                dateDescending,
                page,
                size);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('TUTOR')")
    @Override
    public ResponseEntity<List<AssignmentResponse>> getTutorAssignments(AssignmentStatus status,
                                                                        UUID courseId,
                                                                        String studentName,
                                                                        Boolean dateDescending,
                                                                        Integer page,
                                                                        Integer size) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        UUID userId = userDetails.getId();
        List<AssignmentResponse> response = assignmentService.getTutorAssignmentsWithFilters(status,
                courseId,
                studentName,
                userId,
                dateDescending,
                page,
                size);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('TUTOR')")
    @Override
    public ResponseEntity<Void> updateAssignmentInfo(UUID id, UpdateAssignmentInfoDto updateAssignmentInfoDto) {
        assignmentService.update(id, updateAssignmentInfoDto);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('TUTOR')")
    @Override
    public ResponseEntity<Void> gradeAssignment(UUID id, GradeAssignmentDto gradeAssignmentDto) {
        assignmentService.gradeSolution(id, gradeAssignmentDto);
        return ResponseEntity.ok().build();
    }
}