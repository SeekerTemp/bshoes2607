package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.LoginRequest;
import com.vn.test.bshoes.dto.LoginResponse;
import com.vn.test.bshoes.security.ApiAuthFilter;
import com.vn.test.bshoes.security.SessionRegistry;
import com.vn.test.bshoes.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;
    private final SessionRegistry sessions;

    public AuthController(AuthService service, SessionRegistry sessions) {
        this.service = service;
        this.sessions = sessions;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        LoginResponse r = service.login(req.getTaiKhoan(), req.getMatKhau());
        if (r == null) {
            return ResponseEntity.status(401).build();
        }
        // The token carries this employee's effective quyền, so every later call
        // is checked on the server (ApiAuthFilter) instead of only by the
        // browser's router guard.
        r.setToken(sessions.create(r.getId(), r.getMa(), r.getTen(), r.getVaiTro(), r.getQuyen()));
        return ResponseEntity.ok(r);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(
            @RequestHeader(value = ApiAuthFilter.TOKEN_HEADER, required = false) String token) {
        sessions.remove(token);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    /** Lets the SPA check on start-up whether a stored token is still valid. */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(
            @RequestHeader(value = ApiAuthFilter.TOKEN_HEADER, required = false) String token) {
        SessionRegistry.Session s = sessions.get(token);
        if (s == null) {
            return ResponseEntity.status(401).build();
        }
        // LinkedHashMap, not Map.of: null-tolerant and keeps a stable field order.
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("id", s.idNhanVien());
        body.put("ma", s.ma());
        body.put("ten", s.ten());
        body.put("vaiTro", s.vaiTro());
        body.put("quyen", s.quyen());
        return ResponseEntity.ok(body);
    }
}
