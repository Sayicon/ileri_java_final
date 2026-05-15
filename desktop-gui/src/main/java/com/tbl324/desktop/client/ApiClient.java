package com.tbl324.desktop.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbl324.desktop.model.EventDTO;
import com.tbl324.desktop.model.SeatDTO;
import com.tbl324.desktop.model.TicketDTO;
import com.tbl324.desktop.model.VenueDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ApiClient {

    private final String baseUrl;
    private final HttpClient http;
    private final ObjectMapper mapper;
    private String token;
    private Long userId;
    private String role;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.http    = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.mapper  = new ObjectMapper();
    }

    public void setToken(String token) { this.token = token; }
    public Long getUserId()            { return userId; }
    public String getRole()            { return role; }

    public void login(String username, String password) throws ApiException {
        String body = toJson(Map.of("username", username, "password", password));
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        try {
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() < 200 || resp.statusCode() >= 300)
                throw new ApiException(resp.statusCode());
            JsonNode node = mapper.readTree(resp.body());
            this.token  = node.get("token").asText();
            this.userId = node.get("userId").asLong();
            this.role   = node.has("role") ? node.get("role").asText() : "USER";
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }

    public List<EventDTO> getEvents() throws ApiException {
        HttpRequest.Builder req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/events"))
                .GET();
        if (token != null) req.header("Authorization", "Bearer " + token);
        JsonNode body = sendJson(req.build());
        // response: {success, data: {content: [...]}}
        return mapper.convertValue(body.get("data").get("content"),
                new TypeReference<List<EventDTO>>() {});
    }

    public List<SeatDTO> getSeats(Long eventId) throws ApiException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/events/" + eventId + "/seats"))
                .header("Authorization", "Bearer " + token)
                .GET().build();
        JsonNode body = sendJson(req);
        // response: {success, data: [...]}
        return mapper.convertValue(body.get("data"),
                new TypeReference<List<SeatDTO>>() {});
    }

    public EventDTO createEvent(String title, String description, Long venueId,
                                String startTime, String endTime) throws ApiException {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("title", title);
        m.put("description", description);
        m.put("venueId", venueId);
        m.put("startTime", startTime);
        m.put("endTime", endTime);
        String body = toJson(m);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/events"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        JsonNode node = sendJson(req);
        return mapper.convertValue(node.get("data"), EventDTO.class);
    }

    public List<TicketDTO> getAllTickets() throws ApiException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/tickets"))
                .header("Authorization", "Bearer " + token)
                .GET().build();
        JsonNode body = sendJson(req);
        return mapper.convertValue(body, new TypeReference<List<TicketDTO>>() {});
    }

    public List<TicketDTO> getMyTickets(Long userId) throws ApiException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/tickets/my?userId=" + userId))
                .header("Authorization", "Bearer " + token)
                .GET().build();
        JsonNode body = sendJson(req);
        return mapper.convertValue(body, new TypeReference<List<TicketDTO>>() {});
    }

    public TicketDTO reserve(Long eventId, Long seatId, Long userId) throws ApiException {
        String body = toJson(Map.of("eventId", eventId, "seatId", seatId, "userId", userId));
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/tickets/reserve"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        JsonNode node = sendJson(req);
        return mapper.convertValue(node, TicketDTO.class);
    }

    public void confirmTicket(Long ticketId, String paymentType) throws ApiException {
        String body = toJson(Map.of("paymentType", paymentType));
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/tickets/" + ticketId + "/confirm"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        sendJson(req);
    }

    public void register(String username, String email, String password) throws ApiException {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("username", username);
        m.put("email", email);
        m.put("password", password);
        String body = toJson(m);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        sendVoid(req);
    }

    public List<VenueDTO> getVenues() throws ApiException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/events/venues"))
                .header("Authorization", "Bearer " + token)
                .GET().build();
        JsonNode body = sendJson(req);
        return mapper.convertValue(body.get("data"),
                new TypeReference<List<VenueDTO>>() {});
    }

    public void logout() throws ApiException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/auth/logout"))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        sendVoid(req);
        this.token  = null;
        this.userId = null;
        this.role   = null;
    }

    private String toJson(Map<String, Object> data) throws ApiException {
        try {
            return mapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }

    private JsonNode sendJson(HttpRequest req) throws ApiException {
        try {
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() < 200 || resp.statusCode() >= 300)
                throw new ApiException(resp.statusCode(), resp.body());
            return mapper.readTree(resp.body());
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }

    private void sendVoid(HttpRequest req) throws ApiException {
        try {
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() < 200 || resp.statusCode() >= 300)
                throw new ApiException(resp.statusCode(), resp.body());
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(e);
        }
    }
}
