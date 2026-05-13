package com.tbl324.ticket.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.JedisPool;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
class SeatLockServiceTest {

    static {
        System.setProperty("DOCKER_HOST", "tcp://localhost:2375");
        System.setProperty("DOCKER_API_VERSION", "1.41");
    }

    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>("redis:7.2-alpine").withExposedPorts(6379);

    private SeatLockService seatLockService;

    @BeforeEach
    void setUp() {
        JedisPool pool = new JedisPool(redis.getHost(), redis.getMappedPort(6379));
        seatLockService = new SeatLockService(pool);
    }

    @Test
    void tryLock_firstCall_returnsTrue() {
        boolean locked = seatLockService.tryLock(1L, 10L, "user-1", 30);
        assertThat(locked).isTrue();
    }

    @Test
    void tryLock_doubleLock_sameSeat_returnsFalse() {
        seatLockService.tryLock(1L, 20L, "user-1", 30);
        boolean second = seatLockService.tryLock(1L, 20L, "user-2", 30);
        assertThat(second).isFalse();
    }

    @Test
    void tryLock_differentSeats_bothSucceed() {
        boolean first  = seatLockService.tryLock(1L, 30L, "user-1", 30);
        boolean second = seatLockService.tryLock(1L, 31L, "user-2", 30);
        assertThat(first).isTrue();
        assertThat(second).isTrue();
    }

    @Test
    void release_byOwner_removesLock() {
        seatLockService.tryLock(1L, 40L, "user-1", 30);
        boolean released = seatLockService.release(1L, 40L, "user-1");
        assertThat(released).isTrue();

        boolean relock = seatLockService.tryLock(1L, 40L, "user-2", 30);
        assertThat(relock).isTrue();
    }

    @Test
    void release_byNonOwner_doesNotRemoveLock() {
        seatLockService.tryLock(1L, 50L, "user-1", 30);
        boolean released = seatLockService.release(1L, 50L, "user-2");
        assertThat(released).isFalse();

        boolean relock = seatLockService.tryLock(1L, 50L, "user-3", 30);
        assertThat(relock).isFalse();
    }

    @Test
    void isLocked_returnsCorrectState() {
        assertThat(seatLockService.isLocked(1L, 60L)).isFalse();
        seatLockService.tryLock(1L, 60L, "user-1", 30);
        assertThat(seatLockService.isLocked(1L, 60L)).isTrue();
    }
}
