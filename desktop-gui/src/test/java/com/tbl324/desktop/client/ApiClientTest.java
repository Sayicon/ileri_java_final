package com.tbl324.desktop.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.tbl324.desktop.model.EventDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
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
}
