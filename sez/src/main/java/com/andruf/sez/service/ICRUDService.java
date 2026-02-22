package com.andruf.sez.service;

import com.andruf.sez.entity.IGettableById;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

public interface ICRUDService<E extends IGettableById<I>, CV, UV, I extends Comparable<I>> {

    E getById(@NonNull I id);

    I create(@NonNull CV view);

    I create(@Nullable I id, @NonNull CV view);

    boolean update(@NonNull I id, @NonNull UV view);

    void delete(@NonNull I id);
}