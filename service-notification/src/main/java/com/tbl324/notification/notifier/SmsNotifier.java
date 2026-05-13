package com.tbl324.notification.notifier;

public class SmsNotifier implements Notifier {

    @Override
    public void send(String recipient, String subject, String body) {
        // mock: production would call Twilio/Netgsm
    }
}
