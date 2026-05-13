package com.tbl324.notification.factory;

import com.tbl324.notification.domain.NotificationType;
import com.tbl324.notification.notifier.EmailNotifier;
import com.tbl324.notification.notifier.Notifier;
import com.tbl324.notification.notifier.PushNotifier;
import com.tbl324.notification.notifier.SmsNotifier;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {

    public Notifier getNotifier(NotificationType type) {
        if (type == null) {
            throw new IllegalArgumentException("NotificationType cannot be null");
        }
        return switch (type) {
            case EMAIL -> new EmailNotifier();
            case SMS   -> new SmsNotifier();
            case PUSH  -> new PushNotifier();
        };
    }
}
