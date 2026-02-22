package com.andruf.sez.service;

import com.andruf.sez.criteria.LessonCriteria;
import com.andruf.sez.entity.Enrollment;
import com.andruf.sez.entity.Lesson;
import com.andruf.sez.entity.User;
import com.andruf.sez.entity.enums.LessonStatus;
import com.andruf.sez.entity.enums.UserRole;
import com.andruf.sez.event.NotificationEvent;
import com.andruf.sez.exception.EntityNotFoundException;
import com.andruf.sez.gendto.*;
import com.andruf.sez.mapper.LessonMapper;
import com.andruf.sez.repository.EnrollmentRepository;
import com.andruf.sez.repository.LessonRepository;
import com.andruf.sez.security.services.UserDetailsImpl;
import com.andruf.sez.validator.LessonValidator;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
public class LessonService extends BaseCRUDService<Lesson, CreateLessonDto, UpdateLessonDto, LessonResponse, UUID> {
    @Setter(onMethod = @__({@Autowired}))
    private LessonRepository lessonRepository;
    @Setter(onMethod = @__({@Autowired}))
    private LessonMapper lessonMapper;
    @Setter(onMethod_ = @Autowired)
    private ApplicationEventPublisher eventPublisher;
    @Setter(onMethod_ = @Autowired)
    private LessonValidator lessonValidator;
    @Setter(onMethod_ = @Autowired)
    private NotificationService notificationService;
    @Setter(onMethod_ = @Autowired)
    private EnrollmentRepository enrollmentRepository;

    public List<LessonResponse> getMyLessons(UserDetailsImpl user, LocalDateTime from, LocalDateTime to, String status) {
        LessonCriteria criteria = new LessonCriteria();
        criteria.filterByStartTimeAfter(from);
        criteria.filterByEndTimeBefore(to);
        if(status!=null)
            criteria.filterByStatus(LessonStatus.valueOf(status));
        String authority = user.getAuthorities().iterator().next().getAuthority();
        UserRole role = authority.equals("ROLE_TUTOR")
                ? UserRole.TUTOR
                : UserRole.STUDENT;
        criteria.filterByUserId(user.getId(), role);
        return mapper.toResponseList(criteriaRepository.find(criteria));
    }

    public LessonDetailsResponse getLessonDetails(@NonNull UUID id) {
        Lesson lesson = lessonRepository.findByIdWithFullDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson Not Found: " + id));;
        return lessonMapper.toDetailsResponse(lesson);
    }
    @Override
    @Transactional
    public boolean update(@NonNull UUID id, UpdateLessonDto updateLessonDto) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson Not Found: " + id));

        if (updateLessonDto.getStatus() != null) {
            lesson.setStatus(LessonStatus.valueOf(updateLessonDto.getStatus().toString()));
        }
        if (updateLessonDto.getVideoCallUrl() != null) {
            lesson.setVideoCallUrl(updateLessonDto.getVideoCallUrl().toString());
        }
        lessonRepository.save(lesson);
        if (updateLessonDto.getStartTime() != null || updateLessonDto.getEndTime() != null) {
            Lesson tempLesson = new Lesson();
            tempLesson.setId(lesson.getId());
            tempLesson.setEnrollment(lesson.getEnrollment());
            tempLesson.setStartTime(updateLessonDto.getStartTime().toLocalDateTime());
            tempLesson.setEndTime(updateLessonDto.getEndTime().toLocalDateTime());
            lessonValidator.validate(tempLesson);
            UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            boolean isTutor = currentUser.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TUTOR"));

            User recipient = isTutor ? lesson.getEnrollment().getStudent()
                    : lesson.getEnrollment().getCourse().getTutor();
            java.util.Map<String, String> metadata = new java.util.HashMap<>();
            if (updateLessonDto.getStartTime() != null) {
                metadata.put("newStartTime", updateLessonDto.getStartTime().toString());
            }
            if (updateLessonDto.getEndTime() != null) {
                metadata.put("newEndTime", updateLessonDto.getEndTime().toString());
            }
            CreateActionRequestDto notificationDto = new CreateActionRequestDto();
            notificationDto.setRecipientId(recipient.getId());
            notificationDto.setRelatedEntityId(lesson.getId());
            notificationDto.setActionType(NotificationActionType.LESSON_RESCHEDULE);
            notificationDto.setMessage("Користувач " + currentUser.getUsername() + " пропонує змінити час уроку.");
            notificationDto.setMetadata(metadata);

            notificationService.createActionRequest(notificationDto);
        }
        return true;
    }

    @Transactional
    public void delete(UUID id, UserDetailsImpl userDetails) {
        Lesson lesson = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lesson Not Found: " + id));;
        String studentName = userDetails.getUsername();
        User tutorUser = lesson.getEnrollment().getCourse().getTutor();
        eventPublisher.publishEvent(new NotificationEvent(
                this,
                tutorUser,
                "Студент " + studentName + " скасував урок, запланований на " +
                        lesson.getStartTime().toString()
        ));
    delete(id);
    }

    @Override
    @Transactional
    public UUID create(@NonNull CreateLessonDto createLessonDto) {
        Lesson lesson = mapper.toEntity(createLessonDto);
        Enrollment enrollment = enrollmentRepository.findByIdWithDetails(createLessonDto.getEnrollmentId())
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found: " + createLessonDto.getEnrollmentId()));
        lesson.setEnrollment(enrollment);
        lesson.setStatus(LessonStatus.PLANNED);
        if(lesson.getPrice() == null) {
            lesson.setPrice(enrollment.getCourse().getHourlyRate());
        }
        lessonValidator.validate(lesson);
        Lesson saved = repository.save(lesson);
        User student = enrollment.getStudent();
        String tutorName = enrollment.getCourse().getTutor().getName();
        eventPublisher.publishEvent(new NotificationEvent(
                this,
                student,
                "Урок з " + tutorName + " заплановано на: " +
                        lesson.getStartTime().toString() + " - " + lesson.getEndTime().toString()
        ));

        return saved.getId();
    }
}