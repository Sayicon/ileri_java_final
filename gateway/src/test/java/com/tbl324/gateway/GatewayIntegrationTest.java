package com.tbl324.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void ticketPath_withoutAuth_returns401() {
        webTestClient.get().uri("/api/tickets/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void ticketPath_withAuth_doesNotReturn401() {
        webTestClient.get().uri("/api/tickets/1")
                .header("Authorization", "Bearer test-token")
                .exchange()
                .expectStatus().value(status -> assertNotEquals(401, status));
    }

    @Test
    void eventPath_withoutAuth_doesNotReturn401() {
        webTestClient.get().uri("/api/events")
                .exchange()
                .expectStatus().value(status -> assertNotEquals(401, status));
    }

    @Test
    void authLoginPath_withoutAuth_doesNotReturn401() {
        webTestClient.post().uri("/api/auth/login")
                .exchange()
                .expectStatus().value(status -> assertNotEquals(401, status));
    }
}
