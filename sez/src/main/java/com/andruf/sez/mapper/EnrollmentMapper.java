package com.andruf.sez.mapper;

import com.andruf.sez.config.MapperConfig;
import com.andruf.sez.entity.Enrollment;
import com.andruf.sez.gendto.CreateEnrollmentDto;
import com.andruf.sez.gendto.EnrollmentResponse;
import com.andruf.sez.gendto.UpdateEnrollmentDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = MapperConfig.class, uses = {StudentMapper.class, CourseMapper.class}, builder = @Builder(disableBuilder = true))
public interface EnrollmentMapper extends IBaseMapper<Enrollment, CreateEnrollmentDto, UpdateEnrollmentDto, EnrollmentResponse> {

    @Override
    @Mapping(source = "course.subject.name", target = "subjectName")
    @Mapping(source = "course.tutor.name", target = "tutorName") // Можна додати прізвище через expression
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "student.id", target = "studentId")
    EnrollmentResponse toResponse(Enrollment entity);
}