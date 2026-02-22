package com.andruf.sez.repository;

import com.andruf.sez.entity.Tutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TutorRepository extends IRepository<Tutor, UUID> {
}
