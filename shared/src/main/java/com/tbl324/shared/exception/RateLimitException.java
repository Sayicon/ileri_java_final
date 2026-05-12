package com.tbl324.shared.exception;

public class RateLimitException extends DomainException {

    public RateLimitException(String message) {
        super("RATE_LIMIT_EXCEEDED", 429, message);
    }
}
