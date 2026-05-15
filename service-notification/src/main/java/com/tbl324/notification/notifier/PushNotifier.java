package com.tbl324.notification.notifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushNotifier implements Notifier {

    private static final Logger log = LoggerFactory.getLogger(PushNotifier.class);

    @Override
    public void send(String recipient, String subject, String body) {
        log.info("[PUSH] to={} body={}", recipient, body);
    }
}
