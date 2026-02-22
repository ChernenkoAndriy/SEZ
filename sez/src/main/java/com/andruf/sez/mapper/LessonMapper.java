package com.andruf.sez.mapper;

import com.andruf.sez.config.MapperConfig;
import com.andruf.sez.entity.Lesson;
import com.andruf.sez.gendto.CreateLessonDto;
import com.andruf.sez.gendto.LessonDetailsResponse;
import com.andruf.sez.gendto.LessonResponse;
import com.andruf.sez.gendto.UpdateLessonDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        config = MapperConfig.class,
        uses = {DateTimeMapper.class, EnrollmentMapper.class},
        builder = @Builder(disableBuilder = true))
public interface LessonMapper extends IBaseMapper<Lesson, CreateLessonDto, UpdateLessonDto, LessonResponse> {

    @Override
    @Mapping(source = "enrollment.course.subject.name", target = "subjectName")
    @Mapping(source = "enrollment.course.description", target = "courseName")
    @Mapping(target = "partnerName", ignore = true)
    LessonResponse toResponse(Lesson entity);

    @Mapping(source = "entity", target = "lesson")
    @Mapping(source = "entity.enrollment", target = "enrollmentDetails")
    @Mapping(target = "enrollmentDetails.student.role", ignore = true)
    LessonDetailsResponse toDetailsResponse(Lesson entity);

    @Override
    @Mapping(source = "enrollmentId", target = "enrollment.id")
    Lesson toEntity(CreateLessonDto createDto);

    @AfterMapping
    default void setPartnerName(Lesson entity, @MappingTarget LessonResponse dto) {
        if (entity.getEnrollment() != null && entity.getEnrollment().getStudent() != null) {
            var s = entity.getEnrollment().getStudent();
            dto.setPartnerName(s.getSurname() + " " + s.getName());
        }
    }

    default java.net.URI mapStringToUri(String value) {
        return value != null ? java.net.URI.create(value) : null;
    }

    default String mapUriToString(java.net.URI value) {
        return value != null ? value.toString() : null;
    }
}