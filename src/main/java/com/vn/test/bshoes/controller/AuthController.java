package com.vn.test.bshoes.controller;

import com.vn.test.bshoes.entity.NhanVien;
import com.vn.test.bshoes.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<NhanVien> login(@RequestBody Map<String, String> body) {
        NhanVien nv = service.login(body.get("taiKhoan"), body.get("matKhau"));
        if (nv != null) {
            return ResponseEntity.ok(nv);
        }
        return ResponseEntity.status(401).build();
    }
}
