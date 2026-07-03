package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.dto.LoginRequest;
import com.vn.test.bshoes.dto.LoginResponse;
import com.vn.test.bshoes.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        LoginResponse r = service.login(req.getTaiKhoan(), req.getMatKhau());
        return r != null ? ResponseEntity.ok(r) : ResponseEntity.status(401).build();
    }
}
