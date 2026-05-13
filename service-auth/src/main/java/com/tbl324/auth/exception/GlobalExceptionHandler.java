package com.tbl324.auth.exception;

import com.tbl324.shared.exception.ConflictException;
import com.tbl324.shared.exception.DomainException;
import com.tbl324.shared.exception.NotFoundException;
import com.tbl324.shared.exception.UnauthorizedException;
import com.tbl324.shared.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.computeIfAbsent(fe.getField(), k -> new java.util.ArrayList<>())
                  .add(fe.getDefaultMessage());
        }
        Map<String, Object> body = Map.of(
                "status", 400,
                "title", "Doğrulama hatası",
                "errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex) {
        return errorResponse(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {
        return errorResponse(HttpStatus.UNAUTHORIZED, ex);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        return errorResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleDomainValidation(ValidationException ex) {
        return errorResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        Map<String, Object> body = Map.of(
                "status", 500,
                "title", "Sunucu hatası",
                "detail", ex.getMessage() != null ? ex.getMessage() : "Beklenmeyen hata");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private ResponseEntity<Map<String, Object>> errorResponse(HttpStatus status, DomainException ex) {
        Map<String, Object> body = Map.of(
                "status", status.value(),
                "code",   ex.getErrorCode(),
                "detail", ex.getMessage());
        return ResponseEntity.status(status).body(body);
    }
}
