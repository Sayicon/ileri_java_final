package com.tbl324.notification.notifier;

public interface Notifier {
    void send(String recipient, String subject, String body);
}
