package com.vn.test.bshoes.service;

import com.vn.test.bshoes.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(String taiKhoan, String matKhau);
}
