package com.tbl324.notification.notifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmsNotifier implements Notifier {

    private static final Logger log = LoggerFactory.getLogger(SmsNotifier.class);

    @Override
    public void send(String recipient, String subject, String body) {
        log.info("[SMS] to={} body={}", recipient, body);
    }
}
