package com.tbl324.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
public class AuthGatewayFilter implements GlobalFilter, Ordered {

    // Auth servisi geçiyor — token yok
    private static final List<String> PUBLIC_PREFIXES = List.of("/api/auth");
    // GET /api/events/** herkese açık (etkinlik listesi, koltuklar)
    private static final List<String> PUBLIC_GET_PREFIXES = List.of("/api/events");

    private final ReactiveStringRedisTemplate redisTemplate;

    public AuthGatewayFilter(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path   = exchange.getRequest().getPath().value();
        HttpMethod method = exchange.getRequest().getMethod();

        // Public: auth endpoints
        if (PUBLIC_PREFIXES.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        // Public: GET event/seat endpoints
        if (method == HttpMethod.GET && PUBLIC_GET_PREFIXES.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        // Geri kalan her şey token gerektirir
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return deny(exchange, HttpStatus.UNAUTHORIZED, "Token eksik");
        }

        String token     = authHeader.substring(7);
        String role      = extractClaim(token, "role");
        String sessionId = extractClaim(token, "sessionId");

        if (role == null || sessionId == null) {
            return deny(exchange, HttpStatus.UNAUTHORIZED, "Geçersiz token");
        }

        // O-2: revoke kontrolü
        return redisTemplate.hasKey("revoked:" + sessionId)
                .flatMap(isRevoked -> {
                    if (Boolean.TRUE.equals(isRevoked)) {
                        return deny(exchange, HttpStatus.UNAUTHORIZED, "Token iptal edilmiş");
                    }
                    // O-1: admin-only kontrol
                    if (isAdminOnly(path, method) && !"ADMIN".equals(role)) {
                        return deny(exchange, HttpStatus.FORBIDDEN, "Admin yetkisi gerekli");
                    }
                    return chain.filter(exchange);
                });
    }

    private boolean isAdminOnly(String path, HttpMethod method) {
        // Event mutasyonları (oluştur / güncelle / sil) → sadece admin
        if (path.startsWith("/api/events") && method != HttpMethod.GET) {
            return true;
        }
        // Tüm biletleri getir → sadece admin (kendi biletleri /my ile yapılıyor)
        if (path.equals("/api/tickets") && method == HttpMethod.GET) {
            return true;
        }
        return false;
    }

    /**
     * JWT payload'ından (imza doğrulaması olmadan) claim okur.
     * Token zaten auth servisi tarafından imzalandığından gateway'de sadece role/sessionId okunur.
     */
    private String extractClaim(String token, String claimName) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
            String payload = new String(decoded, StandardCharsets.UTF_8);
            String search  = "\"" + claimName + "\":\"";
            int start = payload.indexOf(search);
            if (start == -1) return null;
            start += search.length();
            int end = payload.indexOf("\"", start);
            return end != -1 ? payload.substring(start, end) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private Mono<Void> deny(ServerWebExchange exchange, HttpStatus status, String detail) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = ("{\"status\":" + status.value() + ",\"detail\":\"" + detail + "\"}")
                .getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
