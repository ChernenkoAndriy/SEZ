package com.andruf.sez.mapper;

import com.andruf.sez.config.MapperConfig;
import com.andruf.sez.entity.Course;
import com.andruf.sez.gendto.CreateCourseDto;
import com.andruf.sez.gendto.CourseResponse;
import com.andruf.sez.gendto.UpdateCourseDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = MapperConfig.class, builder = @Builder(disableBuilder = true))
public interface CourseMapper extends IBaseMapper<Course, CreateCourseDto, UpdateCourseDto, CourseResponse> {

    @Override
    @Mapping(source = "tutor.id", target = "tutorId")
    @Mapping(source = "tutor.name", target = "tutorName")
    @Mapping(source = "tutor.surname", target = "tutorSurname")
    @Mapping(source = "subject.name", target = "subjectName")
    @Mapping(source = "tutor.rating", target = "rating")
    CourseResponse toResponse(Course entity);
}