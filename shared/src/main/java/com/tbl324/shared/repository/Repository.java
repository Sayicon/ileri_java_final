package com.tbl324.shared.repository;

import com.tbl324.shared.api.PagedResult;

import java.util.Optional;

public interface Repository<T, ID> {

    Optional<T> findById(ID id);

    PagedResult<T> findAll(int page, int size);

    T save(T entity);

    void delete(ID id);
}
