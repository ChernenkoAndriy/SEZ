package com.andruf.sez.entity;

import org.springframework.lang.Nullable;

public interface IGettableById<ID extends Comparable<ID>> {
    ID getId();
    void setId(@Nullable ID id);
}