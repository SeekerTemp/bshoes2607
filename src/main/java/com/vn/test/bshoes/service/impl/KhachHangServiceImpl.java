package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.KhachHangDto;
import com.vn.test.bshoes.entity.KhachHang;
import com.vn.test.bshoes.repository.KhachHangRepository;
import com.vn.test.bshoes.service.KhachHangService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KhachHangServiceImpl implements KhachHangService {

    private final KhachHangRepository repo;

    public KhachHangServiceImpl(KhachHangRepository repo) {
        this.repo = repo;
    }

    private KhachHangDto toDto(KhachHang e) {
        return new KhachHangDto(
                e.getId(),
                e.getMaKhachHang(),
                e.getTenKhachHang(),
                e.getGioiTinh(),
                e.getSoDienThoai(),
                e.getEmail(),
                e.getDiaChi(),
                e.getTrangThai()
        );
    }

    @Override
    public List<KhachHangDto> findAll() {
        return repo.findActive().stream().map(this::toDto).toList();
    }

    @Override
    public KhachHangDto findById(int id) {
        return repo.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    public List<KhachHangDto> search(String keyword) {
        return repo.search(keyword).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public KhachHangDto create(KhachHangDto dto) {
        KhachHang e = new KhachHang();
        e.setTenKhachHang(dto.getTen());
        e.setGioiTinh(dto.getGioiTinh());
        e.setSoDienThoai(dto.getSdt());
        e.setEmail(dto.getEmail());
        e.setDiaChi(dto.getDiaChi());
        e.setTrangThai(dto.getTrangThai() == null ? Boolean.TRUE : dto.getTrangThai());
        e.setTrangThaiXoa(false);
        e = repo.save(e);
        e.setMaKhachHang("KH" + e.getId());
        return toDto(repo.save(e));
    }

    @Override
    @Transactional
    public KhachHangDto update(KhachHangDto dto) {
        KhachHang e = repo.findById(dto.getId()).orElseThrow();
        e.setTenKhachHang(dto.getTen());
        e.setGioiTinh(dto.getGioiTinh());
        e.setSoDienThoai(dto.getSdt());
        e.setEmail(dto.getEmail());
        e.setDiaChi(dto.getDiaChi());
        if (dto.getTrangThai() != null) e.setTrangThai(dto.getTrangThai());
        return toDto(repo.save(e));
    }

    @Override
    @Transactional
    public void delete(int id) {
        KhachHang e = repo.findById(id).orElseThrow();
        e.setTrangThaiXoa(true);
        repo.save(e);
    }
}
