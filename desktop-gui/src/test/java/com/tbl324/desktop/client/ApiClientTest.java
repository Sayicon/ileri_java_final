package com.tbl324.desktop.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.tbl324.desktop.model.EventDTO;
import com.tbl324.desktop.model.TicketDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApiClientTest {

    private WireMockServer server;
    private ApiClient apiClient;

    @BeforeEach
    void setUp() {
        server = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        server.start();
        apiClient = new ApiClient("http://localhost:" + server.port());
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void getEvents_success_returnsEventList() throws Exception {
        server.stubFor(get(urlEqualTo("/api/events"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\":true,\"data\":{\"content\":[{\"id\":1,\"title\":\"Konser\",\"description\":\"Harika bir konser\",\"venueId\":1,\"status\":\"ACTIVE\"}]}}")));

        List<EventDTO> events = apiClient.getEvents();

        assertThat(events).hasSize(1);
        assertThat(events.get(0).title()).isEqualTo("Konser");
    }

    @Test
    void getEvents_unauthorized_throwsApiException() {
        server.stubFor(get(urlEqualTo("/api/events"))
                .willReturn(aResponse().withStatus(401)));

        assertThatThrownBy(() -> apiClient.getEvents())
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("401");
    }

    @Test
    void getEvents_serverError_throwsApiException() {
        server.stubFor(get(urlEqualTo("/api/events"))
                .willReturn(aResponse().withStatus(500)));

        assertThatThrownBy(() -> apiClient.getEvents())
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("500");
    }

    @Test
    void getEvents_networkFailure_throwsApiException() {
        server.stop();

        assertThatThrownBy(() -> apiClient.getEvents())
                .isInstanceOf(ApiException.class);
    }

    @Test
    void getMyTickets_success_returnsTicketList() throws Exception {
        apiClient.setToken("test-token");
        server.stubFor(get(urlPathEqualTo("/api/tickets/my"))
                .withQueryParam("userId", equalTo("10"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\":1,\"eventId\":1,\"seatId\":5,\"userId\":10,\"status\":\"CONFIRMED\"}]")));

        List<TicketDTO> tickets = apiClient.getMyTickets(10L);

        assertThat(tickets).hasSize(1);
        assertThat(tickets.get(0).status()).isEqualTo("CONFIRMED");
    }

    @Test
    void reserve_success_returnsTicketDTO() throws Exception {
        apiClient.setToken("test-token");
        server.stubFor(post(urlEqualTo("/api/tickets/reserve"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":42,\"eventId\":1,\"seatId\":5,\"userId\":10,\"status\":\"RESERVED\",\"paymentType\":null}")));

        TicketDTO ticket = apiClient.reserve(1L, 5L, 10L);

        assertThat(ticket.id()).isEqualTo(42L);
        assertThat(ticket.status()).isEqualTo("RESERVED");
    }

    @Test
    void confirmTicket_success_doesNotThrow() throws Exception {
        apiClient.setToken("test-token");
        server.stubFor(post(urlEqualTo("/api/tickets/42/confirm"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\":42,\"eventId\":1,\"seatId\":5,\"userId\":10,\"status\":\"CONFIRMED\",\"paymentType\":\"CASH\"}")));

        assertThatCode(() -> apiClient.confirmTicket(42L, "CASH"))
                .doesNotThrowAnyException();
    }

    @Test
    void confirmTicket_serverError_throwsApiException() {
        apiClient.setToken("test-token");
        server.stubFor(post(urlEqualTo("/api/tickets/99/confirm"))
                .willReturn(aResponse().withStatus(500)));

        assertThatThrownBy(() -> apiClient.confirmTicket(99L, "CASH"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("500");
    }

    @Test
    void getMyTickets_emptyList_returnsEmpty() throws Exception {
        apiClient.setToken("test-token");
        server.stubFor(get(urlPathEqualTo("/api/tickets/my"))
                .withQueryParam("userId", equalTo("99"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        List<TicketDTO> tickets = apiClient.getMyTickets(99L);

        assertThat(tickets).isEmpty();
    }

    @Test
    void createEvent_success_returnsEventDTO() throws Exception {
        apiClient.setToken("test-token");
        server.stubFor(post(urlEqualTo("/api/events"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\":true,\"data\":{\"id\":3,\"title\":\"Yeni Konser\",\"description\":\"Test\",\"venueId\":1,\"status\":\"ACTIVE\"}}")));

        EventDTO event = apiClient.createEvent("Yeni Konser", "Test", 1L,
                "2027-06-01T20:00:00", "2027-06-01T23:00:00");

        assertThat(event.id()).isEqualTo(3L);
        assertThat(event.title()).isEqualTo("Yeni Konser");
    }

    @Test
    void getAllTickets_success_returnsTicketList() throws Exception {
        apiClient.setToken("test-token");
        server.stubFor(get(urlEqualTo("/api/tickets"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"id\":1,\"eventId\":1,\"seatId\":5,\"userId\":10,\"status\":\"CONFIRMED\"},{\"id\":2,\"eventId\":1,\"seatId\":6,\"userId\":11,\"status\":\"PENDING\"}]")));

        List<TicketDTO> tickets = apiClient.getAllTickets();

        assertThat(tickets).hasSize(2);
        assertThat(tickets.get(0).status()).isEqualTo("CONFIRMED");
    }
}
