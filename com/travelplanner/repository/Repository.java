package com.travelplanner.repository;

import com.travelplanner.model.BaseEntity;

import java.util.List;
import java.util.Optional;

public interface Repository<T extends BaseEntity> {
    List<T> findAll();

    Optional<T> findById(int id);

    T save(T entity);

    boolean update(T entity);

    boolean delete(int id);
}
