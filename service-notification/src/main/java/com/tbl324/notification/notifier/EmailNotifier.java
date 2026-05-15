package com.tbl324.notification.notifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailNotifier implements Notifier {

    private static final Logger log = LoggerFactory.getLogger(EmailNotifier.class);

    @Override
    public void send(String recipient, String subject, String body) {
        log.info("[EMAIL] to={} subject={} body={}", recipient, subject, body);
    }
}
