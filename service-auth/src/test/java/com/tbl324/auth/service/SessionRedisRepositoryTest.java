package com.tbl324.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.JedisPool;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers(disabledWithoutDocker = true)
class SessionRedisRepositoryTest {

    static {
        System.setProperty("DOCKER_HOST", "tcp://localhost:2375");
        System.setProperty("DOCKER_API_VERSION", "1.41");
    }

    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>("redis:7.2-alpine").withExposedPorts(6379);

    private SessionRedisRepository repo;

    @BeforeEach
    void setUp() {
        JedisPool pool = new JedisPool(redis.getHost(), redis.getMappedPort(6379));
        repo = new SessionRedisRepository(pool);
    }

    @Test
    void saveSession_thenFind_returnsUserId() {
        repo.saveSession("sess-1", 10L);
        Optional<Long> result = repo.findSession("sess-1");
        assertTrue(result.isPresent());
        assertEquals(10L, result.get());
    }

    @Test
    void findSession_unknownId_returnsEmpty() {
        Optional<Long> result = repo.findSession("nonexistent");
        assertTrue(result.isEmpty());
    }

    @Test
    void revokeSession_thenIsRevoked_returnsTrue() {
        repo.saveSession("sess-2", 20L);
        repo.revokeSession("sess-2");

        assertTrue(repo.isRevoked("sess-2"));
        assertTrue(repo.findSession("sess-2").isEmpty());
    }

    @Test
    void isRevoked_nonRevokedSession_returnsFalse() {
        repo.saveSession("sess-3", 30L);
        assertFalse(repo.isRevoked("sess-3"));
    }
}
