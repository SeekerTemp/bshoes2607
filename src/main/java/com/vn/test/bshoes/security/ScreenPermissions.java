package com.vn.test.bshoes.security;

import java.util.List;
import java.util.Map;

/**
 * Maps an incoming API path to the screen key it belongs to — the same keys the
 * frontend uses in {@code config/screens.js} and the backend stores in
 * {@code nhan_vien_quyen}. That way one permission grid drives both the sidebar
 * and the server-side check, instead of the UI being the only gate.
 *
 * Pure and static so it can be unit-tested without a servlet container.
 */
public final class ScreenPermissions {

    private ScreenPermissions() {
    }

    /**
     * Endpoints anyone may call without logging in: the login handshake itself,
     * health/logging sinks, and everything the public storefront needs (browse
     * products, cart, checkout, look up your own order, place a pre-order).
     *
     * Entries are "METHOD /path-prefix", or "* /path-prefix" for any method.
     */
    private static final List<String> PUBLIC = List.of(
            "* /api/auth/",
            "* /api/ping",
            "POST /api/logs/client",
            "* /api/uploads/",
            // storefront browsing
            "GET /api/san-pham",
            "GET /api/san-pham-chi-tiet/store",
            "GET /api/san-pham-chi-tiet/available",
            "GET /api/san-pham-chi-tiet/by-product/",
            "GET /api/san-pham-chi-tiet/by-ma/",
            "GET /api/loai-san-pham",
            // customer cart / orders / pre-order
            "* /api/gio-hang/",
            "POST /api/dat-truoc"
    );

    /**
     * Longest-prefix-wins map from API path prefix to the screen key that guards
     * it. A path with no entry here needs a valid session but no specific screen
     * permission (e.g. shared attribute lookups used by several screens).
     */
    private static final Map<String, String> SCREEN_BY_PREFIX = Map.ofEntries(
            Map.entry("/api/thong-ke", "dashboard"),
            Map.entry("/api/san-pham-ql", "san-pham"),
            Map.entry("/api/san-pham-chi-tiet", "san-pham"),
            Map.entry("/api/hoa-don-chi-tiet", "hoa-don"),
            Map.entry("/api/hoa-don", "hoa-don"),
            Map.entry("/api/dat-truoc", "dat-truoc"),
            Map.entry("/api/nhan-vien", "nhan-vien"),
            Map.entry("/api/vai-tro", "nhan-vien"),
            Map.entry("/api/khach-hang", "khach-hang"),
            Map.entry("/api/dia-chi", "khach-hang"),
            Map.entry("/api/lich-su-hoa-don", "lich-su"),
            Map.entry("/api/bao-hanh", "bao-hanh"),
            Map.entry("/api/phieu-giam-gia", "phieu-giam-gia")
    );

    /** True when {@code path} may be called without a session token. */
    public static boolean isPublic(String method, String path) {
        if (path == null) {
            return false;
        }
        // Anything outside /api (SPA assets, Thymeleaf pages, static files) is
        // not this filter's business.
        if (!path.startsWith("/api")) {
            return true;
        }
        for (String rule : PUBLIC) {
            int sp = rule.indexOf(' ');
            String ruleMethod = rule.substring(0, sp);
            String prefix = rule.substring(sp + 1);
            boolean methodOk = "*".equals(ruleMethod) || ruleMethod.equalsIgnoreCase(method);
            if (methodOk && matches(path, prefix)) {
                return true;
            }
        }
        return false;
    }

    // A prefix ending in '/' matches by prefix; otherwise it must match the whole
    // path or be followed by '/' or '?', so "/api/san-pham" never swallows
    // "/api/san-pham-ql".
    private static boolean matches(String path, String prefix) {
        if (prefix.endsWith("/")) {
            return path.startsWith(prefix);
        }
        if (!path.startsWith(prefix)) {
            return false;
        }
        if (path.length() == prefix.length()) {
            return true;
        }
        char next = path.charAt(prefix.length());
        return next == '/' || next == '?';
    }

    /** The screen key guarding {@code path}, or null when no specific screen guards it. */
    public static String screenFor(String path) {
        if (path == null) {
            return null;
        }
        String best = null;
        String bestPrefix = null;
        for (Map.Entry<String, String> e : SCREEN_BY_PREFIX.entrySet()) {
            if (matches(path, e.getKey())
                    && (bestPrefix == null || e.getKey().length() > bestPrefix.length())) {
                bestPrefix = e.getKey();
                best = e.getValue();
            }
        }
        return best;
    }

    /**
     * Whether a session holding {@code quyenCsv} (the CSV of screen keys stored
     * per employee; "*" = toàn quyền) may reach {@code screen}.
     */
    public static boolean allows(String quyenCsv, String screen) {
        if (screen == null) {
            return true;
        }
        if (quyenCsv == null) {
            return false;
        }
        if ("*".equals(quyenCsv.trim())) {
            return true;
        }
        for (String part : quyenCsv.split(",")) {
            if (part.trim().equals(screen)) {
                return true;
            }
        }
        return false;
    }
}
