package com.vn.test.bshoes.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Turns business-rule failures (bad args, illegal state like "not enough stock")
 * into 400 responses carrying a readable {@code message}, so the SPA can toast it
 * instead of receiving an opaque 500.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", true, "message", ex.getMessage() == null ? "Yêu cầu không hợp lệ" : ex.getMessage()));
    }
}
