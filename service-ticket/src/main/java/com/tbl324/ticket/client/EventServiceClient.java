package com.tbl324.ticket.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventServiceClient {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper   = new ObjectMapper();

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

    public void updateSeatStatus(Long seatId, String status) {
        try {
            String body = "{\"status\":\"" + status + "\"}";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(eventServiceUrl + "/events/seats/" + seatId + "/status"))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            // koltuk durum güncellemesi kritik değil — bilet akışını bloklama
        }
    }

    public List<Long> getEndedEventIds() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(eventServiceUrl + "/events/ended-ids"))
                    .GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return List.of();
            JsonNode data = mapper.readTree(response.body()).get("data");
            if (data == null || !data.isArray()) return List.of();
            List<Long> ids = new ArrayList<>();
            for (JsonNode n : data) ids.add(n.asLong());
            return ids;
        } catch (Exception e) {
            return List.of();
        }
    }
}
