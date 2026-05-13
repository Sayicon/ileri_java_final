package com.tbl324.ticket.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public abstract class BaseJdbcRepository<T> {

    protected final JdbcTemplate jdbc;

    protected BaseJdbcRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    protected abstract String insertSql();
    protected abstract void setInsertParams(PreparedStatement ps, T entity) throws Exception;
    protected abstract String selectByIdSql();
    protected abstract RowMapper<T> rowMapper();
    protected abstract T withId(T entity, Long id);

    public T save(T entity) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(insertSql(), Statement.RETURN_GENERATED_KEYS);
            try { setInsertParams(ps, entity); } catch (Exception e) { throw new RuntimeException(e); }
            return ps;
        }, keys);
        Long id = keys.getKey().longValue();
        return withId(entity, id);
    }

    public Optional<T> findById(Long id) {
        List<T> rows = jdbc.query(selectByIdSql(), rowMapper(), id);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }
}
