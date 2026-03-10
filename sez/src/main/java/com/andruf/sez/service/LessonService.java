package com.andruf.sez.service;

import com.andruf.sez.criteria.LessonCriteria;
import com.andruf.sez.entity.Enrollment;
import com.andruf.sez.entity.Lesson;
import com.andruf.sez.entity.NotificationMetadata;
import com.andruf.sez.entity.User;
import com.andruf.sez.entity.enums.LessonStatus;
import com.andruf.sez.entity.enums.UserRole;
import com.andruf.sez.event.NotificationEvent;
import com.andruf.sez.exception.EntityNotFoundException;
import com.andruf.sez.gendto.*;
import com.andruf.sez.mapper.LessonMapper;
import com.andruf.sez.repository.AttachmentRepository;
import com.andruf.sez.repository.EnrollmentRepository;
import com.andruf.sez.repository.LessonRepository;
import com.andruf.sez.repository.NotificationRepository;
import com.andruf.sez.security.services.UserDetailsImpl;
import com.andruf.sez.validator.LessonValidator;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;


@RequiredArgsConstructor
@Service
public class LessonService extends BaseCRUDService<Lesson, CreateLessonDto, UpdateLessonDto, LessonResponse, UUID> {
    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final LessonValidator lessonValidator;
    private final NotificationService notificationService;
    private final EnrollmentRepository enrollmentRepository;
    private final AttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;
    private final NotificationRepository notificationRepository;

    public List<LessonResponse> getMyLessons(UserDetailsImpl user, OffsetDateTime from, OffsetDateTime to, String status) {
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
            tempLesson.setStartTime(updateLessonDto.getStartTime());
            tempLesson.setEndTime(updateLessonDto.getEndTime());
            lessonValidator.validate(tempLesson);
            UserDetailsImpl currentUser = (UserDetailsImpl) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            boolean isTutor = currentUser.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TUTOR"));

            User recipient = isTutor ? lesson.getEnrollment().getStudent()
                    : lesson.getEnrollment().getCourse().getTutor();
            List<NotificationMetadataDto> metadata = new ArrayList<>();
            if (updateLessonDto.getStartTime() != null) {
                metadata.add(new NotificationMetadataDto("newStartTime", updateLessonDto.getStartTime().toString()));
            }
            if (updateLessonDto.getEndTime() != null) {
                metadata.add(new NotificationMetadataDto("newEndTime", updateLessonDto.getEndTime().toString()));
            }
            CreateActionRequestDto notificationDto = new CreateActionRequestDto();
            notificationDto.setRecipientId(recipient.getId());
            notificationDto.setRelatedEntityId(lesson.getId());
            notificationDto.setActionType(NotificationActionType.LESSON_RESCHEDULE);
            notificationDto.setMessage("User " + currentUser.getUsername() + " wants to change lesson time.");
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
        notificationRepository.deleteActionRequestsByRelatedEntityIds(java.util.List.of(id));
        delete(id);
        eventPublisher.publishEvent(new NotificationEvent(
                this,
                tutorUser,
                "Student " + studentName + " has cancelled lesson, planned at " +
                        lesson.getStartTime().toString()
        ));
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
                "Lesson with " + tutorName + " is planned at: " +
                        lesson.getStartTime().toString() + " - " + lesson.getEndTime().toString()
        ));

        return saved.getId();
    }

    public void cleanupOldLessons() {
        OffsetDateTime threshold = OffsetDateTime.now().minusWeeks(1);
        List<Lesson> oldLessons = lessonRepository.findAll().stream()
                .filter(l -> l.getEndTime().isBefore(threshold) && l.getStatus() == LessonStatus.CONDUCTED)
                .toList();

        if (oldLessons.isEmpty()) return;
        List<UUID> lessonIds = oldLessons.stream().map(Lesson::getId).toList();
        List<String> fileNames = attachmentRepository.findFileNamesByOldLessons(threshold);
        fileNames.forEach(fileStorageService::deleteFile);
        notificationRepository.deleteActionRequestsByRelatedEntityIds(lessonIds);

        lessonRepository.deleteOldFinishedLessons(threshold);
    }
}