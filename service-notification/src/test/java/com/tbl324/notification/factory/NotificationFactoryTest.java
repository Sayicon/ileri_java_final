package com.tbl324.notification.factory;

import com.tbl324.notification.domain.NotificationType;
import com.tbl324.notification.notifier.EmailNotifier;
import com.tbl324.notification.notifier.Notifier;
import com.tbl324.notification.notifier.PushNotifier;
import com.tbl324.notification.notifier.SmsNotifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificationFactoryTest {

    private final NotificationFactory factory = new NotificationFactory();

    @Test
    void getNotifier_emailType_returnsEmailNotifier() {
        Notifier notifier = factory.getNotifier(NotificationType.EMAIL);
        assertThat(notifier).isInstanceOf(EmailNotifier.class);
    }

    @Test
    void getNotifier_smsType_returnsSmsNotifier() {
        Notifier notifier = factory.getNotifier(NotificationType.SMS);
        assertThat(notifier).isInstanceOf(SmsNotifier.class);
    }

    @Test
    void getNotifier_pushType_returnsPushNotifier() {
        Notifier notifier = factory.getNotifier(NotificationType.PUSH);
        assertThat(notifier).isInstanceOf(PushNotifier.class);
    }

    @Test
    void getNotifier_nullType_throwsIllegalArgument() {
        assertThatThrownBy(() -> factory.getNotifier(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void eachType_returnsDistinctNotifierInstance() {
        Notifier email = factory.getNotifier(NotificationType.EMAIL);
        Notifier sms   = factory.getNotifier(NotificationType.SMS);
        Notifier push  = factory.getNotifier(NotificationType.PUSH);

        assertThat(email).isNotSameAs(sms);
        assertThat(sms).isNotSameAs(push);
    }
}
