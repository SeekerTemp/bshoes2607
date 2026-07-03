package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.NhanVienDto;
import com.vn.test.bshoes.entity.NhanVien;
import com.vn.test.bshoes.entity.VaiTro;
import com.vn.test.bshoes.repository.NhanVienRepository;
import com.vn.test.bshoes.repository.VaiTroRepository;
import com.vn.test.bshoes.service.NhanVienService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienRepository repo;
    private final VaiTroRepository vaiTroRepository;

    public NhanVienServiceImpl(NhanVienRepository repo, VaiTroRepository vaiTroRepository) {
        this.repo = repo;
        this.vaiTroRepository = vaiTroRepository;
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
                e.getTrangThai()
        );
    }

    private void applyVaiTro(NhanVienDto dto, NhanVien e) {
        if (StringUtils.hasText(dto.getVaiTro())) {
            VaiTro vt = vaiTroRepository.findByTenVaiTro(dto.getVaiTro());
            e.setIdVaiTro(vt);
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
        e.setTrangThai(dto.getTrangThai() == null ? Boolean.TRUE : dto.getTrangThai());
        e.setTrangThaiXoa(false);
        applyVaiTro(dto, e);
        e = repo.save(e);
        e.setMaNhanVien("NV" + e.getId());
        return toDto(repo.save(e));
    }

    @Override
    @Transactional
    public NhanVienDto update(NhanVienDto dto) {
        NhanVien e = repo.findById(dto.getId()).orElseThrow();
        e.setTenNhanVien(dto.getTen());
        e.setTaiKhoan(dto.getTaiKhoan());
        e.setEmail(dto.getEmail());
        e.setSoDienThoai(dto.getSdt());
        e.setCccd(dto.getCccd());
        e.setChucVu(dto.getChucVu());
        e.setGioiTinh(dto.getGioiTinh());
        if (dto.getTrangThai() != null) e.setTrangThai(dto.getTrangThai());
        applyVaiTro(dto, e);
        return toDto(repo.save(e));
    }

    @Override
    @Transactional
    public void delete(int id) {
        NhanVien e = repo.findById(id).orElseThrow();
        e.setTrangThaiXoa(true);
        repo.save(e);
    }
}
