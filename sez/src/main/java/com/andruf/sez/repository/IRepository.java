package com.andruf.sez.repository;

import com.andruf.sez.entity.IGettableById;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


import java.util.Optional;

@NoRepositoryBean
public interface IRepository<E extends IGettableById<ID>, ID extends Comparable<ID>> extends JpaRepository<E, ID> {
    Optional<E> findFetchAllById(ID id);
}