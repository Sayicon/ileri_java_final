package com.tbl324.notification.repository;

import com.tbl324.notification.domain.NotificationLog;
import com.tbl324.notification.domain.NotificationStatus;
import com.tbl324.notification.domain.NotificationType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationLogJdbcRepository extends BaseJdbcRepository<NotificationLog> {

    public NotificationLogJdbcRepository(JdbcTemplate jdbc) {
        super(jdbc);
    }

    @Override
    protected String insertSql() {
        return "INSERT INTO notification_logs (type, recipient, subject, body, status, sent_at) VALUES (?,?,?,?,?,?)";
    }

    @Override
    protected Object[] insertParams(NotificationLog log) {
        return new Object[]{
                log.getType().name(),
                log.getRecipient(),
                log.getSubject(),
                log.getBody(),
                log.getStatus().name(),
                log.getSentAt()
        };
    }

    @Override
    protected String selectByIdSql() {
        return "SELECT * FROM notification_logs WHERE id = ?";
    }

    @Override
    protected RowMapper<NotificationLog> rowMapper() {
        return (rs, rowNum) -> NotificationLog.builder()
                .id(rs.getLong("id"))
                .type(NotificationType.valueOf(rs.getString("type")))
                .recipient(rs.getString("recipient"))
                .subject(rs.getString("subject"))
                .body(rs.getString("body"))
                .status(NotificationStatus.valueOf(rs.getString("status")))
                .sentAt(rs.getTimestamp("sent_at").toLocalDateTime())
                .build();
    }
}
