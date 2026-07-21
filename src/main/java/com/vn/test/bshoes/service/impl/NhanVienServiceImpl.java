package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.NhanVienDto;
import com.vn.test.bshoes.entity.NhanVien;
import com.vn.test.bshoes.repository.NhanVienRepository;
import com.vn.test.bshoes.repository.VaiTroRepository;
import com.vn.test.bshoes.service.NhanVienQuyenService;
import com.vn.test.bshoes.service.NhanVienService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienRepository repo;
    private final VaiTroRepository vaiTroRepository;
    private final NhanVienQuyenService quyenService;

    public NhanVienServiceImpl(NhanVienRepository repo, VaiTroRepository vaiTroRepository,
                               NhanVienQuyenService quyenService) {
        this.repo = repo;
        this.vaiTroRepository = vaiTroRepository;
        this.quyenService = quyenService;
    }

    private NhanVienDto toDto(NhanVien e) {
        return new NhanVienDto(
                e.getId(),
                e.getMaNhanVien(),
                e.getTenNhanVien(),
                e.getTaiKhoan(),
                e.getEmail(),
                e.getSoDienThoai(),
                e.getCccd(),
                e.getChucVu(),
                e.getGioiTinh(),
                e.getIdVaiTro() != null ? e.getIdVaiTro().getTenVaiTro() : null,
                e.getTrangThai(),
                e.getIdVaiTro() != null ? e.getIdVaiTro().getId() : null,
                null
        );
    }

    private void applyVaiTro(NhanVienDto dto, NhanVien e) {
        if (dto.getIdVaiTro() != null) {
            e.setIdVaiTro(vaiTroRepository.findById(dto.getIdVaiTro()).orElse(null));
        } else {
            e.setIdVaiTro(null);
        }
    }

    @Override
    public List<NhanVienDto> findAll() {
        return repo.findActive().stream().map(this::toDto).toList();
    }

    @Override
    public NhanVienDto findById(int id) {
        return repo.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    public List<NhanVienDto> search(String ten, String gioiTinh) {
        String gt = StringUtils.hasText(gioiTinh) ? gioiTinh : "all";
        return repo.search(ten, gt).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public NhanVienDto create(NhanVienDto dto) {
        NhanVien e = new NhanVien();
        e.setTenNhanVien(dto.getTen());
        e.setTaiKhoan(dto.getTaiKhoan());
        e.setEmail(dto.getEmail());
        e.setSoDienThoai(dto.getSdt());
        e.setCccd(dto.getCccd());
        e.setChucVu(dto.getChucVu());
        e.setGioiTinh(dto.getGioiTinh());
        e.setMatKhau(dto.getMatKhau());
        e.setTrangThai(dto.getTrangThai() == null ? Boolean.TRUE : dto.getTrangThai());
        e.setTrangThaiXoa(false);
        applyVaiTro(dto, e);
        e = repo.save(e);
        e.setMaNhanVien("NV" + e.getId());
        e = repo.save(e);
        // Không có bước này thì NV mới có 0 dòng trong nhan_vien_quyen -> đăng nhập vào
        // không thấy màn nào. Vật chất hóa bộ quyền mặc định của vai trò ngay khi tạo.
        quyenService.apTemplate(e.getId());
        return toDto(e);
    }

    @Override
    @Transactional
    public NhanVienDto update(NhanVienDto dto) {
        NhanVien e = repo.findById(dto.getId()).orElseThrow();
        Integer vaiTroCu = e.getIdVaiTro() != null ? e.getIdVaiTro().getId() : null;
        e.setTenNhanVien(dto.getTen());
        e.setTaiKhoan(dto.getTaiKhoan());
        e.setEmail(dto.getEmail());
        e.setSoDienThoai(dto.getSdt());
        e.setCccd(dto.getCccd());
        e.setChucVu(dto.getChucVu());
        e.setGioiTinh(dto.getGioiTinh());
        if (org.springframework.util.StringUtils.hasText(dto.getMatKhau())) e.setMatKhau(dto.getMatKhau());
        if (dto.getTrangThai() != null) e.setTrangThai(dto.getTrangThai());
        applyVaiTro(dto, e);
        e = repo.save(e);
        // Đổi vai trò = đổi hẳn bộ quyền: xóa rows cũ, chép template vai trò mới.
        // Phần mở rộng riêng của vai trò cũ mất, có chủ ý (xem spec 2026-07-17).
        Integer vaiTroMoi = e.getIdVaiTro() != null ? e.getIdVaiTro().getId() : null;
        if (!Objects.equals(vaiTroCu, vaiTroMoi)) {
            quyenService.apTemplate(e.getId());
        }
        return toDto(e);
    }

    @Override
    @Transactional
    public void delete(int id) {
        NhanVien e = repo.findById(id).orElseThrow();
        e.setTrangThaiXoa(true);
        repo.save(e);
    }
}
