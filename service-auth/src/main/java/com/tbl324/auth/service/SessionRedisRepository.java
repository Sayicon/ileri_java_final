package com.tbl324.auth.service;

import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Optional;

@Repository
public class SessionRedisRepository {

    private static final long SESSION_TTL = 3600L;

    private final JedisPool jedisPool;

    public SessionRedisRepository(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void saveSession(String sessionId, Long userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex("session:" + sessionId, SESSION_TTL, String.valueOf(userId));
        }
    }

    public Optional<Long> findSession(String sessionId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.get("session:" + sessionId);
            return value != null ? Optional.of(Long.parseLong(value)) : Optional.empty();
        }
    }

    public void revokeSession(String sessionId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del("session:" + sessionId);
            jedis.setex("revoked:" + sessionId, SESSION_TTL, "1");
        }
    }

    public boolean isRevoked(String sessionId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists("revoked:" + sessionId);
        }
    }
}
