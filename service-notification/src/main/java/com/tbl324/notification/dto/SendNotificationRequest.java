package com.tbl324.notification.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tbl324.notification.domain.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SendNotificationRequest {

    @NotNull
    private final NotificationType type;

    @NotBlank
    private final String recipient;

    private final String subject;

    private final String body;

    @JsonCreator
    public SendNotificationRequest(
            @JsonProperty("type")      NotificationType type,
            @JsonProperty("recipient") String recipient,
            @JsonProperty("subject")   String subject,
            @JsonProperty("body")      String body) {
        this.type      = type;
        this.recipient = recipient;
        this.subject   = subject;
        this.body      = body;
    }

    public NotificationType getType()      { return type; }
    public String getRecipient()           { return recipient; }
    public String getSubject()             { return subject; }
    public String getBody()                { return body; }
}
