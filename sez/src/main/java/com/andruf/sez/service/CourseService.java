package com.andruf.sez.service;

import com.andruf.sez.criteria.CourseCriteria;
import com.andruf.sez.entity.Course;
import com.andruf.sez.entity.Subject;
import com.andruf.sez.entity.Tutor;
import com.andruf.sez.exception.EntityNotFoundException;
import com.andruf.sez.gendto.CourseResponse;
import com.andruf.sez.gendto.CreateCourseDto;
import com.andruf.sez.gendto.UpdateCourseDto;
import com.andruf.sez.mapper.CourseMapper;
import com.andruf.sez.repository.SubjectRepository;
import com.andruf.sez.repository.TutorRepository;
import com.andruf.sez.security.services.UserDetailsImpl;
import com.andruf.sez.validator.CourseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CourseService extends BaseCRUDService<Course, CreateCourseDto, UpdateCourseDto, CourseResponse, UUID> {
    @Autowired
    private CourseValidator courseValidator;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private TutorRepository tutorRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    @Transactional
    public List<CourseResponse> getMyCourses(String courseName, UserDetailsImpl userDetails, Integer page, Integer size) {
        CourseCriteria criteria = new CourseCriteria();
        criteria.filterByTutorId(userDetails.getId());
        if(courseName != null && !courseName.isEmpty())
            criteria.filterByCourseName(courseName);
        criteria.setPagination(page != null ? page : 0, size != null ? size : 20);
    return mapper.toResponseList(getList(criteria));
    }
    @Transactional
    public List<CourseResponse> searchCourses(String tutorName,
                                              String courseName,
                                              BigDecimal maxRate,
                                              Boolean excludeEnrolled,
                                              List<String> sortBy,
                                              Integer page,
                                              Integer size,
                                              UUID studentId) {
        CourseCriteria criteria = new CourseCriteria();


        if (excludeEnrolled != null) {
            criteria.filterByEnrollmentStatus(studentId, excludeEnrolled);
        }
        criteria.filterByTutorName(tutorName);
        criteria.filterByCourseName(courseName);
        criteria.filterByMaxRate(maxRate);
        if (sortBy != null && !sortBy.isEmpty()) {
            sortBy.forEach(criteria::applySorting);
        }

        criteria.setPagination(page != null ? page : 0, size != null ? size : 20);
        List<Course> courses = getList(criteria);
        List<CourseResponse> responseList = mapper.toResponseList(courses);

        if (sortBy != null) {
            if (sortBy.contains("rating_desc")) {
                responseList.sort((c1, c2) -> {
                    BigDecimal r1 = c1.getRating() != null ? c1.getRating() : BigDecimal.valueOf(0.0);
                    BigDecimal r2 = c2.getRating() != null ? c2.getRating() : BigDecimal.valueOf(0.0);
                    return r2.compareTo(r1); // Від більшого до меншого
                });
            } else if (sortBy.contains("rating_asc")) {
                responseList.sort((c1, c2) -> {
                    BigDecimal r1 = c1.getRating() != null ? c1.getRating() : BigDecimal.valueOf(0.0);
                    BigDecimal r2 = c2.getRating() != null ? c2.getRating() : BigDecimal.valueOf(0.0);
                    return r1.compareTo(r2); // Від меншого до більшого
                });
            }
        }

        return responseList;
    }

    @Transactional
    public UUID create(@NonNull CreateCourseDto view, UserDetailsImpl userDetails) {
        Course course = courseMapper.toEntity(view);
        UUID tutorid = userDetails.getId();
        Tutor tutor = tutorRepository.findFetchAllById(tutorid)
                .orElseThrow(() -> new EntityNotFoundException("Could not find tutor with: " + tutorid));
        course.setTutor(tutor);
        Subject subject = subjectRepository.findFetchAllById(view.getSubjectId()).orElseThrow(() -> new EntityNotFoundException("Could not find subject with: " + view.getSubjectId()));
        course.setSubject(subject);
                courseValidator.validate(course);
        return repository.save(course).getId();
    }

    @Override
    @Transactional
    public boolean update(@NonNull UUID id, @NonNull UpdateCourseDto view) {
        Course course = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find course with: " + id));
        mapper.updateEntity(course, view);
        repository.save(course);
        return true;
    }
    @Transactional
    public CourseResponse getCourseById(UUID id) {
        return mapper.toResponse(getById(id));
    }
}