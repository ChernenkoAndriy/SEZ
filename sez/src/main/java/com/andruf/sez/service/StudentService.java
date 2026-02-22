package com.andruf.sez.service;

import com.andruf.sez.criteria.StudentCriteria;
import com.andruf.sez.entity.Student;
import com.andruf.sez.gendto.StudentRegistrationDto;
import com.andruf.sez.gendto.StudentResponse;
import com.andruf.sez.gendto.UpdateStudentDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;

@Service
public class StudentService extends BaseCRUDService<Student, StudentRegistrationDto, UpdateStudentDto, StudentResponse, UUID> {
    @Transactional
    public List<StudentResponse> findByCriteria(String name, String courseName, UUID tutorId, Integer page, Integer size) {
        StudentCriteria criteria = new StudentCriteria();
        criteria.filterByTutorId(tutorId);
        criteria.filterByNameOrSurname(name);
        criteria.filterBySubjectName(courseName);
        criteria.setPagination(page != null ? page : 0, size != null ? size : 20);
        return mapper.toResponseList(getList(criteria));
    }
    @Transactional
    public StudentResponse getResponseById(UUID id) {
        return mapper.toResponse(getById(id));
    }
    @Transactional
    public List<StudentResponse> getByCourseId(UUID id, UUID tutorId) {
        StudentCriteria criteria = new StudentCriteria();
        criteria.filterByTutorId(tutorId);
        criteria.filterByCourseId(id);
        List<Student> students = getList(criteria);
        return mapper.toResponseList(students);
    }
}