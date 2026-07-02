package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.NhanVien;

public interface AuthService {
    NhanVien login(String taiKhoan, String matKhau);
}
