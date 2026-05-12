package com.tbl324.shared.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExceptionTest {

    @Test
    void notFoundExceptionHas404Status() {
        NotFoundException ex = new NotFoundException("etkinlik bulunamadı");
        assertEquals(404, ex.getHttpStatus());
        assertEquals("NOT_FOUND", ex.getErrorCode());
    }

    @Test
    void conflictExceptionHas409Status() {
        ConflictException ex = new ConflictException("koltuk zaten dolu");
        assertEquals(409, ex.getHttpStatus());
        assertEquals("CONFLICT", ex.getErrorCode());
    }

    @Test
    void validationExceptionHas400Status() {
        ValidationException ex = new ValidationException("geçersiz email");
        assertEquals(400, ex.getHttpStatus());
        assertEquals("VALIDATION_ERROR", ex.getErrorCode());
    }

    @Test
    void unauthorizedExceptionHas401Status() {
        UnauthorizedException ex = new UnauthorizedException("token gerekli");
        assertEquals(401, ex.getHttpStatus());
        assertEquals("UNAUTHORIZED", ex.getErrorCode());
    }

    @Test
    void forbiddenExceptionHas403Status() {
        ForbiddenException ex = new ForbiddenException("yetki yok");
        assertEquals(403, ex.getHttpStatus());
        assertEquals("FORBIDDEN", ex.getErrorCode());
    }

    @Test
    void rateLimitExceptionHas429Status() {
        RateLimitException ex = new RateLimitException("çok fazla istek");
        assertEquals(429, ex.getHttpStatus());
        assertEquals("RATE_LIMIT_EXCEEDED", ex.getErrorCode());
    }

    @Test
    void exceptionMessageIsPreserved() {
        String message = "test mesajı";
        NotFoundException ex = new NotFoundException(message);
        assertEquals(message, ex.getMessage());
    }
}
