package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.LoginResponse;
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
    // NOTE: plaintext password comparison retained; replace with Spring Security + BCrypt later.
    public LoginResponse login(String taiKhoan, String matKhau) {
        NhanVien n = nhanVienRepository.findByTaiKhoanAndMatKhau(taiKhoan, matKhau);
        if (n == null) return null;
        var vt = n.getIdVaiTro();
        return new LoginResponse(
                n.getId(),
                n.getMaNhanVien(),
                n.getTenNhanVien(),
                vt != null ? vt.getTenVaiTro() : null,
                vt != null ? vt.getId() : null,
                vt != null ? vt.getQuyen() : null
        );
    }
}
