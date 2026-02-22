package com.andruf.sez.criteria;

import com.andruf.sez.entity.Assignment;
import com.andruf.sez.entity.Enrollment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CriteriaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public <E> List<E> find(Criteria<E> criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<E> query = cb.createQuery(criteria.getEntityClass());
        Root<E> root = query.from(criteria.getEntityClass());
        if (!criteria.getEntityClass().equals(Assignment.class)) {
            query.distinct(true);
        }
        if (criteria.getEntityClass().equals(Enrollment.class)) {
            Fetch<Object, Object> courseFetch = root.fetch("course", JoinType.LEFT);
            courseFetch.fetch("subject", JoinType.LEFT);
            courseFetch.fetch("tutor", JoinType.LEFT);
            root.fetch("student", JoinType.LEFT);
        }
        List<Predicate> predicates = criteria.getConditions().stream()
                .map(fn -> fn.toPredicate(root, query, cb))
                .toList();
        query.where(predicates.toArray(new Predicate[0]));

        if (!criteria.getOrders().isEmpty()) {
            List<Order> orders = criteria.getOrders().stream()
                    .map(fn -> fn.toOrder(root, cb))
                    .collect(Collectors.toList());
            query.orderBy(orders);
        }

        TypedQuery<E> typedQuery = entityManager.createQuery(query);

        if (criteria.getPage() != null && criteria.getSize() != null) {
            typedQuery.setFirstResult(criteria.getPage() * criteria.getSize());
            typedQuery.setMaxResults(criteria.getSize());
        }

        return typedQuery.getResultList();
    }

    public <E> long count(Criteria<E> criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<E> root = countQuery.from(criteria.getEntityClass());

        List<Predicate> predicates = criteria.getConditions().stream()
                .map(fn -> fn.toPredicate(root, countQuery, cb))
                .collect(Collectors.toList());

        countQuery.select(cb.count(root)).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}