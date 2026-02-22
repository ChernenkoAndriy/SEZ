package com.andruf.sez.service;


import com.andruf.sez.criteria.AssignmentCriteria;
import com.andruf.sez.entity.Assignment;
import com.andruf.sez.entity.Attachment;
import com.andruf.sez.entity.enums.AssignmentStatus;
import com.andruf.sez.exception.EntityNotFoundException;
import com.andruf.sez.gendto.*;
import com.andruf.sez.repository.AssignmentRepository;
import com.andruf.sez.repository.AttachmentRepository;
import com.andruf.sez.repository.LessonRepository;
import com.andruf.sez.validator.AssignmentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService extends BaseCRUDService<Assignment, CreateAssignmentDto, UpdateAssignmentInfoDto, AssignmentResponse, UUID> {

    private final ApplicationEventPublisher eventPublisher;
    private final LessonRepository lessonRepository;
    private final AssignmentValidator assignmentValidator;
    private final FileStorageService storageService;
    private final AttachmentRepository attachmentRepository;
    private final AssignmentRepository repository;

    @Transactional
    public void createForLesson(UUID lessonId, CreateAssignmentDto dto) {
        Assignment assignment = mapper.toEntity(dto);
        assignment.setStatus(AssignmentStatus.PENDING);
        assignment.setLesson(lessonRepository.findById(lessonId).get());
        assignmentValidator.validateCreation(assignment);
        repository.save(assignment);

    }

    public AssignmentResponse getAssignmentByLesson(UUID lessonId) {
        Assignment assignment = repository.findByLessonId(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found for lesson: " + lessonId));
        return mapper.toResponse(assignment);
    }
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getStudentAssignmentsWithFilters(
            com.andruf.sez.gendto.AssignmentStatus status,
            String courseName,
            String tutorName,
            UUID studentId,
            Boolean dateDescending,
            Integer page,
            Integer size) {
        AssignmentCriteria criteria = new AssignmentCriteria();
        criteria.filterByStudentId(studentId);
        if (status != null) {
            criteria.filterByStatus(AssignmentStatus.valueOf(status.toString()));
        }
        if (courseName != null && !courseName.isBlank()) {
            criteria.filterByCourseName(courseName);
        }
        if (tutorName != null && !tutorName.isBlank()) {
            criteria.filterByTutorName(tutorName);
        }
        criteria.sortByLessonDate(dateDescending == null || !dateDescending);
        criteria.setPagination(page != null ? page : 0, size != null ? size : 20);
        List<Assignment> assignments = getList(criteria);
        return mapper.toResponseList(assignments);
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponse> getTutorAssignmentsWithFilters(
            com.andruf.sez.gendto.AssignmentStatus status,
            UUID courseId,
            String studentName,
            UUID tutorId,
            Boolean dateDescending,
            Integer page,
            Integer size) {
        AssignmentCriteria criteria = new AssignmentCriteria();
        criteria.filterByTutorId(tutorId);
        if (status != null) {
            criteria.filterByStatus(AssignmentStatus.valueOf(status.toString()));
        }
        if (courseId != null) {
            criteria.filterByCourseId(courseId);
        }
        if (studentName != null && !studentName.isBlank()) {
            criteria.filterByStudentName(studentName);
        }
        criteria.sortByLessonDate(dateDescending == null || !dateDescending);
        criteria.setPagination(page != null ? page : 0, size != null ? size : 20);
        List<Assignment> assignments = getList(criteria);
        return mapper.toResponseList(assignments);
    }
@Transactional
    public void submitSolution(UUID id) {
        Assignment assignment = repository.findById(id).get();
        assignmentValidator.validateCompletion(assignment);
        assignment.setStatus(AssignmentStatus.COMPLETED);
        repository.save(assignment);
    }

@Transactional
    public void gradeSolution(UUID id, GradeAssignmentDto dto) {
        Assignment assignment = repository.findById(id).get();
        assignment.setGrade(dto.getGrade());
        assignmentValidator.validateGrading(assignment, dto.getGrade());
    assignment.setStatus(AssignmentStatus.GRADED);
        repository.save(assignment);
    }
    @Transactional
    @Override
    public boolean update(UUID id, UpdateAssignmentInfoDto dto) {
        Assignment assignment = repository.findById(id).get();
        mapper.updateEntity(assignment, dto);
        repository.save(assignment);
        return true;
    }

    @Transactional(readOnly = true)
    public Attachment getAttachmentMetadata(UUID attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found"));
    }

    public Resource loadAttachmentAsResource(String filePath) {
        return storageService.load(filePath);
    }
    @Transactional
    public void addAttachment(UUID assignmentId, MultipartFile file) throws IOException {
        Assignment assignment = repository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));

        String savedPath = storageService.save(file);

        Attachment attachment = Attachment.builder()
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .filePath(savedPath)
                .assignment(assignment)
                .build();

        attachmentRepository.save(attachment);
    }
}