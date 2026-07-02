package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.entity.NhanVien;
import com.vn.test.bshoes.repository.NhanVienRepository;
import com.vn.test.bshoes.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final NhanVienRepository nhanVienRepository;

    public AuthServiceImpl(NhanVienRepository nhanVienRepository) {
        this.nhanVienRepository = nhanVienRepository;
    }

    @Override
    // NOTE: plaintext password comparison retained from legacy; replace with Spring Security + BCrypt later.
    public NhanVien login(String taiKhoan, String matKhau) {
        return nhanVienRepository.login(taiKhoan, matKhau);
    }
}
