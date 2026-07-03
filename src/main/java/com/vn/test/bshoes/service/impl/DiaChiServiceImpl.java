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
    public void delete(int id) {
        repo.deleteById(id);
    }
}
