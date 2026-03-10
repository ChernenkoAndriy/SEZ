package com.andruf.sez.criteria;

import jakarta.persistence.criteria.*;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Criteria<E> {
    private final Class<E> entityClass;
    private final List<PredicateFunction<E>> conditions = new ArrayList<>();
    private final List<OrderFunction<E>> orders = new ArrayList<>();
    private Integer page = null;
    private Integer size = null;
    public Criteria(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public void add(PredicateFunction<E> condition) {
        this.conditions.add(condition);
    }

    public void orderBy(OrderFunction<E> order) {
        this.orders.add(order);
    }

    public void setPagination(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    @FunctionalInterface
    public interface PredicateFunction<E> {
        Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder cb);
    }

    @FunctionalInterface
    public interface OrderFunction<E> {
        Order toOrder(Root<E> root, CriteriaBuilder cb);
    }
}