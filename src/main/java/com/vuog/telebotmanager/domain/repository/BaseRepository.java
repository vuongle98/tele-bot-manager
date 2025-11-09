package com.vuog.telebotmanager.domain.repository;

public interface BaseRepository<T, ID> {

    T save(T entity);

    <S extends T> Iterable<S> saveAll(Iterable<S> entities);

    void delete(T entity);

    void deleteById(ID id);
}
