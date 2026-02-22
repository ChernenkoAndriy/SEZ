package com.andruf.sez.mapper;

import com.andruf.sez.config.MapperConfig;
import com.andruf.sez.entity.Assignment;
import com.andruf.sez.gendto.AssignmentResponse;
import com.andruf.sez.gendto.CreateAssignmentDto;
import com.andruf.sez.gendto.UpdateAssignmentInfoDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        config = MapperConfig.class,
        uses = DateTimeMapper.class,
        builder = @Builder(disableBuilder = true))
public interface AssignmentMapper extends IBaseMapper<Assignment, CreateAssignmentDto, UpdateAssignmentInfoDto, AssignmentResponse> {
    @Override
    @Mapping(source = "lesson.id", target = "lessonId")
    @Mapping(source = "lesson.startTime", target = "lessonStartTime")
    @Mapping(target = "tutorFullName", expression = "java(entity.getLesson().getEnrollment().getCourse().getTutor().getSurname() + \" \" + entity.getLesson().getEnrollment().getCourse().getTutor().getName())")
    @Mapping(target = "studentFullName", expression = "java(entity.getLesson().getEnrollment().getStudent().getSurname() + \" \" + entity.getLesson().getEnrollment().getStudent().getName())")
    AssignmentResponse toResponse(Assignment entity);
}