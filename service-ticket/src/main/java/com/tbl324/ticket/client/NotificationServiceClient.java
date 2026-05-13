package com.tbl324.ticket.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@Component
public class NotificationServiceClient {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${services.notification.url:http://localhost:8084}")
    private String notificationServiceUrl;

    public CompletableFuture<Void> sendAsync(String recipient, String body) {
        String json = "{\"type\":\"EMAIL\",\"recipient\":\"" + recipient +
                      "\",\"subject\":\"Bilet Bilgisi\",\"body\":\"" + body + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(notificationServiceUrl + "/notifications/send"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenApply(r -> null);
    }
}
