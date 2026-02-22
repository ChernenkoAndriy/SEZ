package com.andruf.sez.mapper;

import com.andruf.sez.config.MapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;
public interface IBaseMapper<E, C, U, R> {
    E toEntity(C createDto);

    void updateEntity(@MappingTarget E entity, U updateDto);

    R toResponse(E entity);

    List<R> toResponseList(List<E> entities);
}
