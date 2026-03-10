package com.andruf.sez.service;

import com.andruf.sez.entity.Tutor;
import com.andruf.sez.gendto.TutorRegistrationDto;
import com.andruf.sez.gendto.TutorResponse;
import com.andruf.sez.gendto.UpdateTutorDto;
import com.andruf.sez.repository.ReviewRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;

@Service
public class TutorService extends BaseCRUDService<Tutor, TutorRegistrationDto, UpdateTutorDto, TutorResponse, UUID> {
    @Setter(onMethod_ = @Autowired)
    private ReviewRepository reviewRepository;
    public List<TutorResponse> getAllTutorResponses() {
        return mapper.toResponseList(super.getAll());
    }
    public TutorResponse getTutorResponseById(UUID id) {
        return mapper.toResponse(super.getById(id));
    }
    @Transactional
    public void recalculateAllRatings() {
        List<Tutor> tutors = repository.findAll();
        for (Tutor tutor : tutors) {
            Double averageRating = reviewRepository.getAverageRatingForTutor(tutor.getId());
            if (averageRating != null) {
                tutor.setRating(averageRating);
                repository.save(tutor);
            }
        }
    }
}