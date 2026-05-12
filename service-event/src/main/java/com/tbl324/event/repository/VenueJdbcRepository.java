package com.tbl324.event.repository;

import com.tbl324.event.domain.Venue;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class VenueJdbcRepository extends BaseJdbcRepository<Venue, Long> {

    public VenueJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected String getTableName() {
        return "venues";
    }

    @Override
    protected Venue mapRow(ResultSet rs) throws SQLException {
        return Venue.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .address(rs.getString("address"))
                .capacity(rs.getInt("capacity"))
                .build();
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO venues (name, address, capacity) VALUES (?, ?, ?)";
    }

    @Override
    protected void bindInsert(PreparedStatement ps, Venue venue) throws SQLException {
        ps.setString(1, venue.getName());
        ps.setString(2, venue.getAddress());
        ps.setInt(3, venue.getCapacity());
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE venues SET name = ?, address = ?, capacity = ? WHERE id = ?";
    }

    @Override
    protected void bindUpdate(PreparedStatement ps, Venue venue) throws SQLException {
        ps.setString(1, venue.getName());
        ps.setString(2, venue.getAddress());
        ps.setInt(3, venue.getCapacity());
        ps.setLong(4, venue.getId());
    }

    @Override
    protected Long getId(Venue venue) {
        return venue.getId();
    }
}
