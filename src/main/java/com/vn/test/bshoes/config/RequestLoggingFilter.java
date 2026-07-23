package com.vn.test.bshoes.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Logs one line per /api/** request (method, URI, status, duration) so that,
 * after a test run on a deployed machine, logs/bshoes.log has a record of API
 * activity that can be handed over for diagnosis. Failed requests (>= 400) are
 * logged at WARN with the (redacted) request body attached.
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    private static final int MAX_BODY_LOG_CHARS = 2000;

    // Cache limit for ContentCachingRequestWrapper itself (bytes); generous
    // enough for our JSON API bodies. The logged excerpt is separately capped
    // at MAX_BODY_LOG_CHARS above.
    private static final int REQUEST_CACHE_LIMIT_BYTES = 65536;

    // Redact common sensitive fields ("matKhau"/"password"/"token") wherever
    // they appear as a JSON string/number value, e.g. "matKhau":"abc123" -> "matKhau":"***"
    private static final Pattern SENSITIVE_FIELD = Pattern.compile(
            "(?i)(\"(?:matKhau|password|token)\"\\s*:\\s*)\"[^\"]*\"");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        boolean multipart = isMultipart(request);
        // Don't wrap the request body for multipart uploads: reading/caching
        // binary file content is useless for diagnosis and expensive, and we
        // never touch the body below when it's multipart anyway.
        HttpServletRequest requestToUse = multipart
                ? request
                : new ContentCachingRequestWrapper(request, REQUEST_CACHE_LIMIT_BYTES);
        ContentCachingResponseWrapper responseToUse = new ContentCachingResponseWrapper(response);

        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(requestToUse, responseToUse);
        } finally {
            long durationMs = System.currentTimeMillis() - start;
            try {
                logRequest(requestToUse, responseToUse, multipart, durationMs);
            } catch (Exception loggingFailure) {
                // Never let logging break the actual response.
                log.warn("Failed to log request", loggingFailure);
            }
            // MUST happen after we've read the cached body and regardless of
            // outcome, or the real response body never reaches the client.
            responseToUse.copyBodyToResponse();
        }
    }

    private void logRequest(HttpServletRequest request, ContentCachingResponseWrapper response,
                             boolean multipart, long durationMs) {
        int status = response.getStatus();
        String line = request.getMethod() + " " + request.getRequestURI() + " -> " + status + " (" + durationMs + "ms)";

        if (status >= 400) {
            String body = multipart ? "[multipart, not captured]" : extractBody(request);
            log.warn("{} body={}", line, body);
        } else {
            log.info(line);
        }
    }

    private String extractBody(HttpServletRequest request) {
        if (!(request instanceof ContentCachingRequestWrapper wrapper)) {
            return "";
        }
        byte[] buf = wrapper.getContentAsByteArray();
        if (buf.length == 0) {
            return "";
        }
        String charset = wrapper.getCharacterEncoding() != null ? wrapper.getCharacterEncoding() : StandardCharsets.UTF_8.name();
        String raw;
        try {
            raw = new String(buf, charset);
        } catch (Exception e) {
            raw = new String(buf, StandardCharsets.UTF_8);
        }
        String redacted = SENSITIVE_FIELD.matcher(raw).replaceAll("$1\"***\"");
        if (redacted.length() > MAX_BODY_LOG_CHARS) {
            redacted = redacted.substring(0, MAX_BODY_LOG_CHARS) + "...(truncated)";
        }
        return redacted;
    }

    private boolean isMultipart(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith("multipart/");
    }
}
