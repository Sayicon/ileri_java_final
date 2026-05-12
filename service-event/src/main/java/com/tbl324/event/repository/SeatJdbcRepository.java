package com.tbl324.event.repository;

import com.tbl324.event.domain.Seat;
import com.tbl324.event.domain.SeatStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SeatJdbcRepository extends BaseJdbcRepository<Seat, Long> {

    public SeatJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getTableName() {
        return "seats";
    }

    @Override
    protected Seat mapRow(ResultSet rs) throws SQLException {
        return Seat.builder()
                .id(rs.getLong("id"))
                .venueId(rs.getLong("venue_id"))
                .rowLabel(rs.getString("row_label"))
                .seatNumber(rs.getInt("seat_number"))
                .status(SeatStatus.valueOf(rs.getString("status")))
                .build();
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO seats (venue_id, row_label, seat_number, status) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected void bindInsert(PreparedStatement ps, Seat seat) throws SQLException {
        ps.setLong(1, seat.getVenueId());
        ps.setString(2, seat.getRowLabel());
        ps.setInt(3, seat.getSeatNumber());
        ps.setString(4, seat.getStatus().name());
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE seats SET venue_id = ?, row_label = ?, seat_number = ?, status = ? WHERE id = ?";
    }

    @Override
    protected void bindUpdate(PreparedStatement ps, Seat seat) throws SQLException {
        bindInsert(ps, seat);
        ps.setLong(5, seat.getId());
    }

    @Override
    protected Long getId(Seat seat) {
        return seat.getId();
    }

    public List<Seat> findByVenueId(Long venueId) {
        String sql = "SELECT * FROM seats WHERE venue_id = ? ORDER BY row_label, seat_number";
        List<Seat> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, venueId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByVenueId failed", e);
        }
        return result;
    }
}
