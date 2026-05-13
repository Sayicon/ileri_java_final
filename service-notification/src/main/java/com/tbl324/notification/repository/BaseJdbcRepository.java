package com.tbl324.notification.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Optional;

public abstract class BaseJdbcRepository<T> {

    protected final JdbcTemplate jdbc;

    protected BaseJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    protected abstract String insertSql();
    protected abstract Object[] insertParams(T entity);
    protected abstract String selectByIdSql();
    protected abstract RowMapper<T> rowMapper();

    public void save(T entity) {
        jdbc.update(insertSql(), insertParams(entity));
    }

    public Optional<T> findById(Long id) {
        return jdbc.query(selectByIdSql(), rowMapper(), id)
                   .stream()
                   .findFirst();
    }
}
