package com.vn.test.bshoes.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store of logged-in sessions, keyed by an opaque token the client
 * sends back in the {@code X-Auth-Token} header.
 *
 * Deliberately simple for this prototype: no JWT, no server-side persistence, so
 * restarting the backend logs everyone out. What it DOES give us is a real
 * server-side check — before this, the permission grid only existed in the
 * browser and any client could call any admin endpoint directly.
 */
@Component
public class SessionRegistry {

    /** Sessions idle for longer than this are dropped. */
    private static final Duration TTL = Duration.ofHours(8);

    public record Session(Integer idNhanVien, String ma, String ten, String vaiTro, String quyen, Instant lastSeen) {
        Session touch() {
            return new Session(idNhanVien, ma, ten, vaiTro, quyen, Instant.now());
        }
    }

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    /** Registers a new session and returns its token. */
    public String create(Integer idNhanVien, String ma, String ten, String vaiTro, String quyen) {
        purgeExpired();
        String token = UUID.randomUUID().toString().replace("-", "");
        sessions.put(token, new Session(idNhanVien, ma, ten, vaiTro, quyen, Instant.now()));
        return token;
    }

    /** Returns the live session for {@code token}, or null when absent/expired. */
    public Session get(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        Session s = sessions.get(token);
        if (s == null) {
            return null;
        }
        if (isExpired(s)) {
            sessions.remove(token);
            return null;
        }
        Session touched = s.touch();
        sessions.put(token, touched);
        return touched;
    }

    public void remove(String token) {
        if (token != null) {
            sessions.remove(token);
        }
    }

    public int size() {
        return sessions.size();
    }

    private boolean isExpired(Session s) {
        return s.lastSeen().plus(TTL).isBefore(Instant.now());
    }

    private void purgeExpired() {
        sessions.entrySet().removeIf(e -> isExpired(e.getValue()));
    }
}
