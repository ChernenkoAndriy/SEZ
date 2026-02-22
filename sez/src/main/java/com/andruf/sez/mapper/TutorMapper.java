package com.andruf.sez.mapper;

import com.andruf.sez.config.MapperConfig;
import com.andruf.sez.entity.Tutor;
import com.andruf.sez.gendto.TutorRegistrationDto;
import com.andruf.sez.gendto.TutorResponse;
import com.andruf.sez.gendto.UpdateTutorDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        config = MapperConfig.class,
        builder = @Builder(disableBuilder = true))
public interface TutorMapper extends IBaseMapper<Tutor, TutorRegistrationDto, UpdateTutorDto, TutorResponse> {
}