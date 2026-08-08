package com.vn.test.bshoes.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Enforces, on the server, the same screen permissions the sidebar shows: an
 * /api call that guards a screen needs a valid session token whose quyền list
 * includes that screen.
 *
 * Before this filter existed the router guard in the browser was the only check,
 * so anyone could curl an admin endpoint directly.
 *
 * Implemented as a plain servlet filter on purpose: spring-boot-starter-security
 * is commented out in pom.xml, and pulling it in would change behaviour across
 * the whole app for no gain here — the permission model lives in
 * {@code nhan_vien_quyen}, not in Spring roles.
 *
 * PROTOTYPE SCOPE: passwords are still compared in plaintext (AuthServiceImpl)
 * and tokens live in memory only. Deliberate for user testing; both must change
 * before this handles real accounts.
 */
@Component
@Order(1)
public class ApiAuthFilter extends OncePerRequestFilter {

    public static final String TOKEN_HEADER = "X-Auth-Token";

    private static final Logger log = LoggerFactory.getLogger(ApiAuthFilter.class);

    private final SessionRegistry sessions;

    public ApiAuthFilter(SessionRegistry sessions) {
        this.sessions = sessions;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // CORS preflight carries no headers to authenticate with.
        if ("OPTIONS".equalsIgnoreCase(method) || ScreenPermissions.isPublic(method, path)) {
            chain.doFilter(request, response);
            return;
        }

        SessionRegistry.Session session = sessions.get(request.getHeader(TOKEN_HEADER));
        if (session == null) {
            log.debug("401 {} {} — thiếu hoặc sai token", method, path);
            deny(response, 401, "Bạn cần đăng nhập để dùng chức năng này");
            return;
        }

        String screen = ScreenPermissions.screenFor(path);
        if (!ScreenPermissions.allows(session.quyen(), screen)) {
            log.debug("403 {} {} — {} không có quyền màn '{}'", method, path, session.ma(), screen);
            deny(response, 403, "Tài khoản của bạn không có quyền dùng chức năng này");
            return;
        }

        request.setAttribute("bshoes.session", session);
        chain.doFilter(request, response);
    }

    // Same JSON error shape GlobalExceptionHandler uses, so the frontend's
    // crudErrorMessage() picks up the message unchanged.
    private void deny(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":true,\"message\":\"" + message + "\"}");
    }
}
