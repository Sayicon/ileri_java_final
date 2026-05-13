package com.tbl324.notification.domain;

import java.time.LocalDateTime;

public class NotificationLog {

    private final Long id;
    private final NotificationType type;
    private final String recipient;
    private final String subject;
    private final String body;
    private final NotificationStatus status;
    private final LocalDateTime sentAt;

    private NotificationLog(Builder b) {
        this.id        = b.id;
        this.type      = b.type;
        this.recipient = b.recipient;
        this.subject   = b.subject;
        this.body      = b.body;
        this.status    = b.status;
        this.sentAt    = b.sentAt;
    }

    public Long getId()                  { return id; }
    public NotificationType getType()    { return type; }
    public String getRecipient()         { return recipient; }
    public String getSubject()           { return subject; }
    public String getBody()              { return body; }
    public NotificationStatus getStatus(){ return status; }
    public LocalDateTime getSentAt()     { return sentAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private NotificationType type;
        private String recipient;
        private String subject;
        private String body;
        private NotificationStatus status;
        private LocalDateTime sentAt;

        public Builder id(Long id)                        { this.id = id; return this; }
        public Builder type(NotificationType type)        { this.type = type; return this; }
        public Builder recipient(String recipient)        { this.recipient = recipient; return this; }
        public Builder subject(String subject)            { this.subject = subject; return this; }
        public Builder body(String body)                  { this.body = body; return this; }
        public Builder status(NotificationStatus status)  { this.status = status; return this; }
        public Builder sentAt(LocalDateTime sentAt)       { this.sentAt = sentAt; return this; }
        public NotificationLog build()                    { return new NotificationLog(this); }
    }
}
