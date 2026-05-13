package com.tbl324.notification.notifier;

public class EmailNotifier implements Notifier {

    @Override
    public void send(String recipient, String subject, String body) {
        // mock: production would call SMTP/SendGrid
    }
}
