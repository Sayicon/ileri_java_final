package com.tbl324.event.repository;

import com.tbl324.shared.api.PagedResult;
import com.tbl324.shared.repository.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseJdbcRepository<T, ID> implements Repository<T, ID> {

    protected final DataSource dataSource;

    protected BaseJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected abstract String getTableName();

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected abstract String getInsertSql();

    protected abstract void bindInsert(PreparedStatement ps, T entity) throws SQLException;

    protected abstract String getUpdateSql();

    protected abstract void bindUpdate(PreparedStatement ps, T entity) throws SQLException;

    protected abstract ID getId(T entity);

    @Override
    public Optional<T> findById(ID id) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById failed", e);
        }
    }

    // Sayfalı sorgu — Repository interface karşılığı
    @Override
    public PagedResult<T> findAll(int page, int size) {
        List<T> items = findList(page * size, size);
        long total = count();
        return PagedResult.of(items, page, size, total);
    }

    // Offset/limit tabanlı ham liste — Repository testleri ve service için
    public List<T> findList(int offset, int limit) {
        String sql = "SELECT * FROM " + getTableName() + " LIMIT ? OFFSET ?";
        List<T> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("findList failed", e);
        }
        return result;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM " + getTableName();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new RuntimeException("count failed", e);
        }
    }

    @Override
    public T save(T entity) {
        if (getId(entity) == null) {
            return insert(entity);
        }
        return update(entity);
    }

    @SuppressWarnings("unchecked")
    private T insert(T entity) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(getInsertSql(), Statement.RETURN_GENERATED_KEYS)) {
            bindInsert(ps, entity);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    return findById((ID) (Long) id).orElseThrow();
                }
                throw new RuntimeException("insert: generated key not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("insert failed", e);
        }
    }

    private T update(T entity) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(getUpdateSql())) {
            bindUpdate(ps, entity);
            ps.executeUpdate();
            return findById(getId(entity)).orElseThrow();
        } catch (SQLException e) {
            throw new RuntimeException("update failed", e);
        }
    }

    @Override
    public void delete(ID id) {
        String sql = "DELETE FROM " + getTableName() + " WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("delete failed", e);
        }
    }
}
