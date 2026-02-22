package com.andruf.sez.service;

import com.andruf.sez.criteria.EnrollmentCriteria;
import com.andruf.sez.entity.Course;
import com.andruf.sez.entity.Enrollment;
import com.andruf.sez.entity.Student;
import com.andruf.sez.entity.User;
import com.andruf.sez.entity.enums.EnrollmentStatus;
import com.andruf.sez.exception.EntityNotFoundException;
import com.andruf.sez.gendto.*;
import com.andruf.sez.mapper.CourseMapper;
import com.andruf.sez.mapper.StudentMapper;
import com.andruf.sez.repository.CourseRepository;
import com.andruf.sez.repository.EnrollmentRepository;
import com.andruf.sez.repository.StudentRepository;
import com.andruf.sez.security.services.UserDetailsImpl;
import com.andruf.sez.validator.EnrollmentValidator;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;

@Service
public class EnrollmentService extends BaseCRUDService<Enrollment, CreateEnrollmentDto, UpdateEnrollmentDto, EnrollmentResponse, UUID> {

    @Setter(onMethod_ = @Autowired)
    private EnrollmentRepository enrollmentRepository;
    @Setter(onMethod_ = @Autowired)
    private StudentMapper studentMapper;
    @Setter(onMethod_ = @Autowired)
    private CourseRepository courseRepository;
    @Setter(onMethod_ = @Autowired)
    private StudentRepository studentRepository;
    @Setter(onMethod_ = @Autowired)
    private CourseMapper courseMapper;
    @Setter(onMethod_ = @Autowired)
    private EnrollmentValidator enrollmentValidator;
    @Setter(onMethod_ = @Autowired)
    private NotificationService notificationService;
    @Override
    @Transactional
    public UUID create(@NonNull CreateEnrollmentDto dto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Student student = studentRepository.findById(userDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + userDetails.getId()));
        com.andruf.sez.entity.Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + dto.getCourseId()));
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(EnrollmentStatus.REQUESTED);
        enrollmentValidator.validateCreation(enrollment);
        Enrollment saved = repository.save(enrollment);
        User tutor = saved.getCourse().getTutor();
        CreateActionRequestDto actionRequest = new CreateActionRequestDto();
        actionRequest.setRecipientId(tutor.getId());
        actionRequest.setRelatedEntityId(saved.getId());
        actionRequest.setActionType(NotificationActionType.ENROLLMENT_CONFIRM);
        actionRequest.setMessage("Студент " + student.getName() + " хоче записатися на ваш курс: "
                + saved.getCourse().getSubject().getName());

        notificationService.createActionRequest(actionRequest);

        return saved.getId();
    }
    @Transactional
    public List<EnrollmentResponse> getEnrollments(UserDetailsImpl user, String studentName, String tutorName, String courseName, Integer page, Integer size) {
    EnrollmentCriteria criteria = new EnrollmentCriteria();
    criteria.filterByStatus(EnrollmentStatus.ACTIVE);
    criteria.filterByStudentName(studentName);
    criteria.filterByTutorName(tutorName);
    criteria.setPagination(page != null ? page : 0, size != null ? size : 10);
    return mapper.toResponseList(getList(criteria));
    }

    @Transactional
    public void terminateAndCleanup(UUID id, UUID userId) {
        delete(id);
    }

    public EnrollmentDetailsResponse getEnrollmentDetails(UUID id) {
        Enrollment enrollment = enrollmentRepository.findByIdWithDetails(id).get();
        EnrollmentDetailsResponse details = new EnrollmentDetailsResponse();
        details.setId(enrollment.getId());
        details.setStatus(EnrollmentDetailsResponse.StatusEnum.fromValue(enrollment.getStatus().toString()));
        details.setStudent(studentMapper.toResponse(enrollment.getStudent()));
        details.setCourse(courseMapper.toResponse(enrollment.getCourse()));
        return details;
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDetailsResponse> getMyActiveEnrollments(
            UserDetailsImpl user,
            String studentName,
            String courseName) {

        EnrollmentCriteria criteria = new EnrollmentCriteria();
        criteria.filterByStatus(EnrollmentStatus.ACTIVE);
        criteria.filterByTutorId(user.getId());
        if (studentName != null && !studentName.isBlank()) {
            criteria.filterByStudentName(studentName);
        }
        if (courseName != null && !courseName.isBlank()) {
            criteria.filterBySubjectName(courseName);
        }
        List<Enrollment> enrollments = criteriaRepository.find(criteria);
        return enrollments.stream()
                .map(this::mapToDetailsResponse)
                .toList();
    }

    private EnrollmentDetailsResponse mapToDetailsResponse(Enrollment enrollment) {
        EnrollmentDetailsResponse details = new EnrollmentDetailsResponse();
        details.setId(enrollment.getId());
        details.setStatus(EnrollmentDetailsResponse.StatusEnum.fromValue(enrollment.getStatus().toString()));
        details.setStudent(studentMapper.toResponse(enrollment.getStudent()));
        details.setCourse(courseMapper.toResponse(enrollment.getCourse()));
        return details;
    }

    @Transactional
    public boolean update(UUID id, UpdateEnrollmentDto updateEnrollmentDto) {
        Enrollment enrollment = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found: " + id));
        enrollment.setStatus(EnrollmentStatus.valueOf(updateEnrollmentDto.getStatus().toString()));
        repository.save(enrollment);
        return true;
    }

    @Transactional
    public void terminateEnrollment(UUID courseId, UUID id) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found: " + id));
        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found for student: " + id + " and course: " + courseId));
        enrollmentRepository.delete(enrollment);
    }
}