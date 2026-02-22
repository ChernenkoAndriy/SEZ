package com.andruf.sez.mapper;

import com.andruf.sez.config.MapperConfig;
import com.andruf.sez.entity.Student;
import com.andruf.sez.gendto.StudentRegistrationDto;
import com.andruf.sez.gendto.StudentResponse;
import com.andruf.sez.gendto.UpdateStudentDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = MapperConfig.class, builder = @Builder(disableBuilder = true))
public interface StudentMapper extends IBaseMapper<Student, StudentRegistrationDto, UpdateStudentDto, StudentResponse> {
    @Override
    @Mapping(target = "role", constant = "STUDENT")
    StudentResponse toResponse(Student entity);
}