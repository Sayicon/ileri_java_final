package com.tbl324.ticket.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class EventServiceClient {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${services.event.url:http://localhost:8082}")
    private String eventServiceUrl;

    public boolean eventExists(Long eventId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(eventServiceUrl + "/events/" + eventId))
                    .GET()
                    .build();
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
