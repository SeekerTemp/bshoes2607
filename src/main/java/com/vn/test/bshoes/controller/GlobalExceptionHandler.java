package com.vn.test.bshoes.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

/**
 * Turns business-rule failures (bad args, illegal state like "not enough stock")
 * into 400 responses carrying a readable {@code message}, so the SPA can toast it
 * instead of receiving an opaque 500.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(RuntimeException ex, HttpServletRequest request) {
        log.warn("400 on {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", true, "message", ex.getMessage() == null ? "Yêu cầu không hợp lệ" : ex.getMessage()));
    }

    // A multipart just over the servlet limit (e.g. an image near/over 1MB with
    // boundary overhead) would otherwise surface as an opaque 500 — turn it into
    // the same friendly 400 the upload endpoint uses for its own size check.
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleUploadTooLarge(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.warn("400 on {} {}: upload too large ({})", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", true, "message", "Ảnh phải nhỏ hơn 1MB"));
    }

    // Catch-all: anything else (NPE, DB errors, etc.) used to fall through to
    // Spring's default opaque 500 with no trace in our log and nothing usable
    // for the SPA's crudErrorMessage(response.data.message). Log the full stack
    // trace for diagnosis and still hand the frontend the same JSON shape.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("500 on {} {}", request.getMethod(), request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", true, "message", "Đã xảy ra lỗi hệ thống, vui lòng thử lại sau"));
    }
}
