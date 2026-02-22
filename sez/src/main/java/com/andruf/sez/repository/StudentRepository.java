package com.andruf.sez.repository;

import com.andruf.sez.entity.Student;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudentRepository extends IRepository<Student,UUID>, JpaSpecificationExecutor<Student> {
}
