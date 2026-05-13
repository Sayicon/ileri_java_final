package com.tbl324.ticket.service;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

@Service
public class SeatLockService {

    private final JedisPool jedisPool;

    public SeatLockService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    private String key(Long eventId, Long seatId) {
        return "lock:seat:" + eventId + ":" + seatId;
    }

    public boolean tryLock(Long eventId, Long seatId, String ownerId, long ttlSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            String result = jedis.set(key(eventId, seatId), ownerId,
                    SetParams.setParams().nx().ex(ttlSeconds));
            return "OK".equals(result);
        }
    }

    public boolean release(Long eventId, Long seatId, String ownerId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String current = jedis.get(key(eventId, seatId));
            if (ownerId.equals(current)) {
                jedis.del(key(eventId, seatId));
                return true;
            }
            return false;
        }
    }

    public boolean isLocked(Long eventId, Long seatId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key(eventId, seatId));
        }
    }
}
