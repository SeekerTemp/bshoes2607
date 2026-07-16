package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.DiaChiDto;
import com.vn.test.bshoes.entity.DiaChi;
import com.vn.test.bshoes.entity.KhachHang;
import com.vn.test.bshoes.repository.DiaChiRepository;
import com.vn.test.bshoes.repository.KhachHangRepository;
import com.vn.test.bshoes.service.DiaChiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DiaChiServiceImpl implements DiaChiService {

    private final DiaChiRepository repo;
    private final KhachHangRepository khachHangRepository;

    public DiaChiServiceImpl(DiaChiRepository repo, KhachHangRepository khachHangRepository) {
        this.repo = repo;
        this.khachHangRepository = khachHangRepository;
    }

    private DiaChiDto toDto(DiaChi e) {
        return new DiaChiDto(
                e.getId(),
                e.getIdKhachHang() != null ? e.getIdKhachHang().getId() : null,
                e.getDiaChiMacDinh(),
                e.getThanhPho(),
                e.getPhuong(),
                e.getDiaChiThem(),
                e.getTrangThai()
        );
    }

    @Override
    public List<DiaChiDto> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public DiaChiDto findById(int id) {
        return repo.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    public List<DiaChiDto> findByKhachHang(int idKhachHang) {
        return repo.findByKhachHang(idKhachHang).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public DiaChiDto create(DiaChiDto dto) {
        DiaChi e = new DiaChi();
        e.setDiaChiMacDinh(dto.getDiaChiMacDinh());
        e.setThanhPho(dto.getThanhPho());
        e.setPhuong(dto.getPhuong());
        e.setDiaChiThem(dto.getDiaChiThem());
        e.setTrangThai(dto.getTrangThai() == null ? Boolean.TRUE : dto.getTrangThai());
        e.setTrangThaiXoa(false);
        if (dto.getIdKhachHang() != null) {
            KhachHang kh = khachHangRepository.getReferenceById(dto.getIdKhachHang());
            e.setIdKhachHang(kh);
        }
        return toDto(repo.save(e));
    }

    @Override
    @Transactional
    public DiaChiDto update(int id, DiaChiDto dto) {
        DiaChi e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Địa chỉ không tồn tại."));
        if (dto.getDiaChiMacDinh() != null) e.setDiaChiMacDinh(dto.getDiaChiMacDinh());
        if (dto.getThanhPho() != null) e.setThanhPho(dto.getThanhPho());
        if (dto.getPhuong() != null) e.setPhuong(dto.getPhuong());
        if (dto.getDiaChiThem() != null) e.setDiaChiThem(dto.getDiaChiThem());
        if (dto.getTrangThai() != null) e.setTrangThai(dto.getTrangThai());
        return toDto(repo.save(e));
    }

    @Override
    @Transactional
    public void delete(int id) {
        repo.deleteById(id);
    }
}
