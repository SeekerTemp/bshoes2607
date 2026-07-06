package com.vn.test.bshoes.controller;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DB-free connectivity endpoint — used to verify JSON round-trips between the
 * Vite dev server (:5173, proxying /api) and this Spring Boot app (:8085)
 * without needing the SQL Server database.
 */
@RestController
@RequestMapping("/api/ping")
public class PingController {

    @GetMapping
    public Map<String, Object> ping() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "UP");
        body.put("service", "bshoes");
        body.put("message", "Xin chào từ Spring Boot");
        body.put("time", Instant.now().toString());
        return body;
    }

    /** Echo the posted JSON back, to prove request+response JSON both directions. */
    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody(required = false) Map<String, Object> payload) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("received", payload);
        body.put("echoedAt", Instant.now().toString());
        return body;
    }
}
