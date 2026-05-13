package com.tbl324.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;
    private TokenService shortLivedService;
    private static final String SECRET =
            Base64.getEncoder().encodeToString(new byte[64]); // 512-bit key

    @BeforeEach
    void setUp() {
        tokenService = new TokenService(SECRET, 3_600_000L);
        shortLivedService = new TokenService(SECRET, 1L); // 1ms — expires immediately
    }

    @Test
    void generateToken_returnsNonEmptyString() {
        String token = tokenService.generateToken(1L, "kerem", "USER", "sess-1");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void parseToken_validToken_claimsMatch() {
        String token = tokenService.generateToken(42L, "efe", "ADMIN", "sess-2");
        Claims claims = tokenService.parseToken(token);

        assertEquals("42", claims.getSubject());
        assertEquals("efe", claims.get("username", String.class));
        assertEquals("ADMIN", claims.get("role", String.class));
        assertEquals("sess-2", claims.get("sessionId", String.class));
    }

    @Test
    void isTokenExpired_freshToken_returnsFalse() {
        String token = tokenService.generateToken(1L, "user", "USER", "s");
        assertFalse(tokenService.isTokenExpired(token));
    }

    @Test
    void isTokenExpired_expiredToken_returnsTrue() throws InterruptedException {
        String token = shortLivedService.generateToken(1L, "user", "USER", "s");
        Thread.sleep(50);
        assertTrue(shortLivedService.isTokenExpired(token));
    }

    @Test
    void parseToken_tamperedSignature_throwsJwtException() {
        String token = tokenService.generateToken(1L, "user", "USER", "s");
        String tampered = token.substring(0, token.lastIndexOf('.') + 1) + "tampered";
        assertThrows(JwtException.class, () -> tokenService.parseToken(tampered));
    }

    @Test
    void getSessionId_validToken_returnsCorrectSessionId() {
        String token = tokenService.generateToken(1L, "user", "USER", "my-session");
        assertEquals("my-session", tokenService.getSessionId(token));
    }
}
