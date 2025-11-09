package com.vuog.telebotmanager.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface BaseQueryRepository<T, ID> {
    T getById(ID id);

    Optional<T> findById(ID id);

    List<T> findAll(Specification<T> spec);

    List<T> findAll();

    Page<T> findAll(Specification<T> spec, Pageable pageable);

    long count(Specification<T> spec);
}
