package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.ClientLogBatchDto;
import com.vn.test.bshoes.dto.ClientLogEntryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Ingests batches of frontend log entries (frontend/src/utils/logger.js
 * flushLogs()) and writes them into the SAME rolling log file as backend
 * events (logback-spring.xml -> ${LOG_DIR}/bshoes.log), under a dedicated
 * "CLIENT" logger so a single collected file has both sides of a deploy test
 * run, interleaved by time.
 *
 * This is a logging sink: it must NEVER throw and must never let a bad/huge
 * payload blow up the log file, so it caps batch size and per-field length
 * and swallows all errors, always answering 200.
 */
@RestController
@RequestMapping("/api/logs")
public class ClientLogController {

    // Dedicated logger name (not this class's) so lines are tagged distinctly
    // from backend logger categories while landing in the same appenders/file.
    private static final Logger clientLog = LoggerFactory.getLogger("CLIENT");

    private static final int MAX_ENTRIES_PER_REQUEST = 200;
    private static final int MAX_MESSAGE_CHARS = 500;
    private static final int MAX_DETAIL_CHARS = 2000;

    @PostMapping("/client")
    public ResponseEntity<Map<String, Object>> ingest(@RequestBody(required = false) ClientLogBatchDto batch) {
        int received = 0;
        try {
            if (batch != null && batch.getLogs() != null && !batch.getLogs().isEmpty()) {
                List<ClientLogEntryDto> entries = batch.getLogs();
                int limit = Math.min(entries.size(), MAX_ENTRIES_PER_REQUEST);
                for (int i = 0; i < limit; i++) {
                    writeEntry(entries.get(i));
                    received++;
                }
            }
        } catch (Exception e) {
            // A logging endpoint must never break the client, no matter what.
            clientLog.warn("Failed to ingest client log batch", e);
        }
        return ResponseEntity.ok(Map.of("received", received));
    }

    private void writeEntry(ClientLogEntryDto entry) {
        if (entry == null) {
            return;
        }
        String category = safe(entry.getCategory());
        String message = truncate(safe(entry.getMessage()), MAX_MESSAGE_CHARS);
        String route = safe(entry.getRoute());
        String user = safe(entry.getUser());
        String t = safe(entry.getT());
        String detail = truncate(safe(entry.getDetail()), MAX_DETAIL_CHARS);

        String level = entry.getLevel() == null ? "" : entry.getLevel().toLowerCase();
        switch (level) {
            case "error" -> clientLog.error("[{}] {} | route={} user={} | {} | detail={}",
                    category, message, route, user, t, detail);
            case "warn" -> clientLog.warn("[{}] {} | route={} user={} | {} | detail={}",
                    category, message, route, user, t, detail);
            default -> clientLog.info("[{}] {} | route={} user={} | {} | detail={}",
                    category, message, route, user, t, detail);
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String truncate(String value, int max) {
        if (value == null || value.length() <= max) {
            return value;
        }
        return value.substring(0, max) + "...(truncated)";
    }
}
