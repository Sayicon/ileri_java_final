package com.tbl324.auth.repository;

import com.tbl324.auth.domain.User;
import com.tbl324.auth.domain.UserRole;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@Repository
public class UserJdbcRepository extends BaseJdbcRepository<User, Long> {

    public UserJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getTableName() { return "users"; }

    @Override
    protected User mapRow(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .username(rs.getString("username"))
                .email(rs.getString("email"))
                .passwordHash(rs.getString("password_hash"))
                .role(UserRole.valueOf(
                        queryRoleName(rs.getLong("role_id"))))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .active(rs.getBoolean("active"))
                .build();
    }

    private String queryRoleName(long roleId) {
        String sql = "SELECT name FROM roles WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("name") : "USER";
            }
        } catch (SQLException e) {
            throw new RuntimeException("queryRoleName failed", e);
        }
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO users (username, email, password_hash, role_id, created_at, active) " +
               "VALUES (?, ?, ?, (SELECT id FROM roles WHERE name = ?), NOW(), TRUE)";
    }

    @Override
    protected void bindInsert(PreparedStatement ps, User u) throws SQLException {
        ps.setString(1, u.getUsername());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getPasswordHash());
        ps.setString(4, u.getRole().name());
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE users SET username = ?, email = ?, password_hash = ?, active = ? WHERE id = ?";
    }

    @Override
    protected void bindUpdate(PreparedStatement ps, User u) throws SQLException {
        ps.setString(1, u.getUsername());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getPasswordHash());
        ps.setBoolean(4, u.isActive());
        ps.setLong(5, u.getId());
    }

    @Override
    protected Long getId(User u) { return u.getId(); }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByUsername failed", e);
        }
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("existsByUsername failed", e);
        }
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("existsByEmail failed", e);
        }
    }
}
