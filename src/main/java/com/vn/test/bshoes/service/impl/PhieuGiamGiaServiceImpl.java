package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.PhieuGiamGiaDto;
import com.vn.test.bshoes.entity.PhieuGiamGia;
import com.vn.test.bshoes.repository.PhieuGiamGiaRepository;
import com.vn.test.bshoes.service.PhieuGiamGiaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PhieuGiamGiaServiceImpl implements PhieuGiamGiaService {

    private final PhieuGiamGiaRepository repo;

    public PhieuGiamGiaServiceImpl(PhieuGiamGiaRepository repo) {
        this.repo = repo;
    }

    private PhieuGiamGiaDto toDto(PhieuGiamGia e) {
        PhieuGiamGiaDto dto = new PhieuGiamGiaDto();
        dto.setId(e.getId());
        dto.setMa(e.getMaPhieuGiam());
        dto.setTen(e.getTenPhieuGiam());
        dto.setLoai(e.getLoaiGiamGia());
        dto.setGiaTri(e.getGiaTriGiam());
        dto.setDonToiThieu(e.getDonToiThieu());
        dto.setGiamToiDa(e.getGiamToiDa());
        dto.setSoLuong(e.getSoLuong());
        dto.setBatDau(e.getThoiGianBatDau() != null ? e.getThoiGianBatDau().toString() : null);
        dto.setKetThuc(e.getThoiGianKetThuc() != null ? e.getThoiGianKetThuc().toString() : null);
        dto.setTrangThai(e.getTrangThai());
        return dto;
    }

    private java.time.Instant parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return java.time.Instant.parse(s);
        } catch (Exception ex) {
            return java.time.LocalDate.parse(s).atStartOfDay(java.time.ZoneOffset.UTC).toInstant();
        }
    }

    private void apply(PhieuGiamGiaDto dto, PhieuGiamGia e) {
        e.setTenPhieuGiam(dto.getTen());
        e.setLoaiGiamGia(dto.getLoai());
        e.setGiaTriGiam(dto.getGiaTri());
        e.setDonToiThieu(dto.getDonToiThieu());
        e.setGiamToiDa(dto.getGiamToiDa());
        e.setSoLuong(dto.getSoLuong());
        e.setThoiGianBatDau(parseDate(dto.getBatDau()));
        e.setThoiGianKetThuc(parseDate(dto.getKetThuc()));
        e.setTrangThai(dto.getTrangThai());
    }

    @Override
    public List<PhieuGiamGiaDto> findAll() {
        return repo.findActive().stream().map(this::toDto).toList();
    }

    @Override
    public PhieuGiamGiaDto findById(int id) {
        return repo.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    public List<PhieuGiamGiaDto> search(String kw) {
        return repo.search(kw).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public PhieuGiamGiaDto create(PhieuGiamGiaDto dto) {
        PhieuGiamGia e = new PhieuGiamGia();
        apply(dto, e);
        e.setTrangThaiXoa(false);
        if (dto.getTrangThai() == null) e.setTrangThai(true);
        e = repo.save(e);
        e.setMaPhieuGiam("PGG" + e.getId());
        return toDto(repo.save(e));
    }

    @Override
    @Transactional
    public PhieuGiamGiaDto update(PhieuGiamGiaDto dto) {
        PhieuGiamGia e = repo.findById(dto.getId()).orElseThrow();
        apply(dto, e);
        return toDto(repo.save(e));
    }

    @Override
    @Transactional
    public void delete(int id) {
        PhieuGiamGia e = repo.findById(id).orElseThrow();
        e.setTrangThaiXoa(true);
        repo.save(e);
    }
}
