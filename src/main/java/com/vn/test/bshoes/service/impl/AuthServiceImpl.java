package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.LoginResponse;
import com.vn.test.bshoes.entity.NhanVien;
import com.vn.test.bshoes.repository.NhanVienRepository;
import com.vn.test.bshoes.service.AuthService;
import com.vn.test.bshoes.service.NhanVienQuyenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final NhanVienRepository nhanVienRepository;
    private final NhanVienQuyenService quyenService;

    public AuthServiceImpl(NhanVienRepository nhanVienRepository, NhanVienQuyenService quyenService) {
        this.nhanVienRepository = nhanVienRepository;
        this.quyenService = quyenService;
    }

    @Override
    @Transactional(readOnly = true)
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
                // Quyền hiệu lực của CHÍNH nhân viên này (rows nhan_vien_quyen), không phải
                // template của vai trò. ADMIN -> "*", useAuth tự giãn theo SCREENS.
                quyenService.quyenCsv(n.getId())
        );
    }
}
