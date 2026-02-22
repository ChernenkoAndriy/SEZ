package com.andruf.sez.service;

import com.andruf.sez.criteria.Criteria;
import com.andruf.sez.criteria.CriteriaRepository;
import com.andruf.sez.entity.IGettableById;
import com.andruf.sez.mapper.IBaseMapper;
import com.andruf.sez.repository.IRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public abstract class BaseCRUDService<
        E extends IGettableById<I>,
        CV,
        UV,
        RV,
        I extends Comparable<I>>
        implements ICRUDService<E, CV, UV, I> {

    @Setter(onMethod_ = @Autowired)
    protected IRepository<E, I> repository;
    @Setter(onMethod_ = @Autowired)
    protected CriteriaRepository criteriaRepository;
    @Setter(onMethod_ = @Autowired)
    protected IBaseMapper<E, CV, UV, RV> mapper;

    @Override
    @Transactional(readOnly = true)
    public E getById(@NonNull I id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entity not found with id: " + id));
    }
    @Override
    @Transactional
    public I create(@NonNull CV view) {
        E entity = mapper.toEntity(view);
        postCreate(entity, view);
        E saved = repository.save(entity);
        return saved.getId();
    }
    @Override
    @Transactional
    public I create(@Nullable I id, @NonNull CV view) {
        E entity = mapper.toEntity(view);
        if (id != null) {
            entity.setId(id);
        }

        postCreate(entity, view);
        E saved = repository.save(entity);
        return saved.getId();
    }
    @Override
    @Transactional
    public boolean update(@NonNull I id, @NonNull UV view) {
        Optional<E> optional = repository.findById(id);
        if (optional.isEmpty()) {
            return false;
        }

        E entity = optional.get();
        mapper.updateEntity(entity, view);
        postUpdate(entity, view);

        repository.save(entity);
        return true;
    }
    @Override
    @Transactional
    public void delete(@NonNull I id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Entity not found with id: " + id);
        }
        repository.deleteById(id);
    }
    @Transactional
    public List<E> getAll() {
        return repository.findAll();
    }
    @Transactional(readOnly = true)
    public List<E> getList(Criteria<E> criteria) {
        return criteriaRepository.find(criteria);
    }
    protected void postCreate(@NonNull E entity, @NonNull CV view) {
    }

    protected void postUpdate(@NonNull E entity, @NonNull UV view) {
    }

}