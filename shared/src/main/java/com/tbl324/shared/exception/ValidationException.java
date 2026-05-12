package com.tbl324.shared.exception;

public class ValidationException extends DomainException {

    public ValidationException(String message) {
        super("VALIDATION_ERROR", 400, message);
    }
}
