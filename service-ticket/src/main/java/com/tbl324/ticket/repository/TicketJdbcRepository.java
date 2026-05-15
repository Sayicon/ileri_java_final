package com.tbl324.ticket.repository;

import com.tbl324.ticket.domain.TicketStatus;
import com.tbl324.ticket.dto.TicketDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class TicketJdbcRepository extends BaseJdbcRepository<TicketDTO> {

    public TicketJdbcRepository(JdbcTemplate jdbc) {
        super(jdbc);
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO tickets (event_id, seat_id, user_id, status, reserved_at, expires_at) " +
               "VALUES (?, ?, ?, ?, NOW(), NOW() + INTERVAL '10 minutes')";
    }

    @Override
    protected void setInsertParams(PreparedStatement ps, TicketDTO t) throws Exception {
        ps.setLong(1, t.eventId());
        ps.setLong(2, t.seatId());
        ps.setLong(3, t.userId());
        ps.setString(4, t.status().name());
    }

    @Override
    protected String selectByIdSql() {
        return "SELECT id, event_id, seat_id, user_id, status FROM tickets WHERE id = ?";
    }

    @Override
    protected RowMapper<TicketDTO> rowMapper() {
        return (rs, rn) -> new TicketDTO(
                rs.getLong("id"),
                rs.getLong("event_id"),
                rs.getLong("seat_id"),
                rs.getLong("user_id"),
                TicketStatus.valueOf(rs.getString("status"))
        );
    }

    @Override
    protected TicketDTO withId(TicketDTO t, Long id) {
        return new TicketDTO(id, t.eventId(), t.seatId(), t.userId(), t.status());
    }

    @Override
    public TicketDTO save(TicketDTO dto) {
        if (dto.id() == null) {
            KeyHolder keys = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(insertSql(), new String[]{"id"});
                try { setInsertParams(ps, dto); } catch (Exception e) { throw new RuntimeException(e); }
                return ps;
            }, keys);
            return withId(dto, ((Number) keys.getKeys().get("id")).longValue());
        } else {
            jdbc.update("UPDATE tickets SET status = ? WHERE id = ?", dto.status().name(), dto.id());
            return dto;
        }
    }

    public List<TicketDTO> findByUserId(Long userId) {
        return jdbc.query(
                "SELECT id, event_id, seat_id, user_id, status FROM tickets WHERE user_id = ? ORDER BY id DESC",
                rowMapper(), userId);
    }

    public List<TicketDTO> findAll() {
        return jdbc.query(
                "SELECT id, event_id, seat_id, user_id, status FROM tickets ORDER BY id DESC",
                rowMapper());
    }

    public void deleteExpired() {
        jdbc.update("UPDATE tickets SET status = 'EXPIRED' WHERE status = 'PENDING' AND expires_at < ?",
                Timestamp.valueOf(LocalDateTime.now()));
    }
}
