package com.tbl324.event.repository;

import com.tbl324.event.domain.Event;
import com.tbl324.event.domain.EventStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventJdbcRepository extends BaseJdbcRepository<Event, Long> {

    public EventJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getTableName() {
        return "events";
    }

    @Override
    protected Event mapRow(ResultSet rs) throws SQLException {
        return Event.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .venueId(rs.getLong("venue_id"))
                .startTime(rs.getTimestamp("start_time").toLocalDateTime())
                .endTime(rs.getTimestamp("end_time").toLocalDateTime())
                .totalSeats(rs.getInt("total_seats"))
                .availableSeats(rs.getInt("available_seats"))
                .status(EventStatus.valueOf(rs.getString("status")))
                .build();
    }

    @Override
    protected String getInsertSql() {
        return """
                INSERT INTO events (title, description, venue_id, start_time, end_time,
                                    total_seats, available_seats, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
    }

    @Override
    protected void bindInsert(PreparedStatement ps, Event event) throws SQLException {
        ps.setString(1, event.getTitle());
        ps.setString(2, event.getDescription());
        ps.setLong(3, event.getVenueId());
        ps.setTimestamp(4, Timestamp.valueOf(event.getStartTime()));
        ps.setTimestamp(5, Timestamp.valueOf(event.getEndTime()));
        ps.setInt(6, event.getTotalSeats());
        ps.setInt(7, event.getAvailableSeats());
        ps.setString(8, event.getStatus().name());
    }

    @Override
    protected String getUpdateSql() {
        return """
                UPDATE events SET title = ?, description = ?, venue_id = ?, start_time = ?,
                                  end_time = ?, total_seats = ?, available_seats = ?, status = ?
                WHERE id = ?
                """;
    }

    @Override
    protected void bindUpdate(PreparedStatement ps, Event event) throws SQLException {
        bindInsert(ps, event);
        ps.setLong(9, event.getId());
    }

    @Override
    protected Long getId(Event event) {
        return event.getId();
    }

    public List<Long> findEndedIds() {
        String sql = "SELECT id FROM events WHERE end_time < NOW()";
        List<Long> ids = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ids.add(rs.getLong(1));
        } catch (SQLException e) {
            throw new RuntimeException("findEndedIds failed", e);
        }
        return ids;
    }
}
