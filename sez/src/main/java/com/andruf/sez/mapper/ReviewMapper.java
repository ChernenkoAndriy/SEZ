package com.andruf.sez.mapper;

import com.andruf.sez.config.MapperConfig;
import com.andruf.sez.entity.Review;
import com.andruf.sez.gendto.CreateReviewDto;
import com.andruf.sez.gendto.ReviewResponse;
import com.andruf.sez.gendto.UpdateReviewDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = MapperConfig.class, uses = DateTimeMapper.class, builder = @Builder(disableBuilder = true))
public interface ReviewMapper extends IBaseMapper<Review, CreateReviewDto, UpdateReviewDto, ReviewResponse> {

    @Override
    @Mapping(target = "tutorId", source = "tutor.id")
    @Mapping(target = "tutorName", source = "tutor.name")
    @Mapping(target = "tutorSurname", source = "tutor.surname")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.name")
    @Mapping(target = "studentSurname", source = "student.surname")
    ReviewResponse toResponse(Review entity);
}