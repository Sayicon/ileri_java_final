package com.tbl324.notification.service;

import com.tbl324.notification.domain.NotificationLog;
import com.tbl324.notification.domain.NotificationStatus;
import com.tbl324.notification.dto.SendNotificationRequest;
import com.tbl324.notification.factory.NotificationFactory;
import com.tbl324.notification.notifier.Notifier;
import com.tbl324.notification.repository.NotificationLogJdbcRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private final NotificationFactory factory;
    private final NotificationLogJdbcRepository repository;

    public NotificationService(NotificationFactory factory,
                                NotificationLogJdbcRepository repository) {
        this.factory    = factory;
        this.repository = repository;
    }

    public void send(SendNotificationRequest req) {
        Notifier notifier = factory.getNotifier(req.getType());
        NotificationStatus status = NotificationStatus.FAILED;
        try {
            notifier.send(req.getRecipient(), req.getSubject(), req.getBody());
            status = NotificationStatus.SENT;
        } catch (Exception e) {
            throw e;
        } finally {
            NotificationLog log = NotificationLog.builder()
                    .type(req.getType())
                    .recipient(req.getRecipient())
                    .subject(req.getSubject())
                    .body(req.getBody())
                    .status(status)
                    .sentAt(LocalDateTime.now())
                    .build();
            repository.save(log);
        }
    }
}
