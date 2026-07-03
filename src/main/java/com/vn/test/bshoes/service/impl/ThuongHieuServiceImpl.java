package com.vn.test.bshoes.service.impl;
import com.vn.test.bshoes.dto.AttributeMoTaDto;
import com.vn.test.bshoes.entity.ThuongHieu;
import com.vn.test.bshoes.repository.ThuongHieuRepository;
import com.vn.test.bshoes.service.ThuongHieuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ThuongHieuServiceImpl implements ThuongHieuService {
    private final ThuongHieuRepository repo;
    public ThuongHieuServiceImpl(ThuongHieuRepository repo) { this.repo = repo; }

    private AttributeMoTaDto toDto(ThuongHieu e) {
        return new AttributeMoTaDto(e.getId(), e.getMaThuongHieu(), e.getTenThuongHieu(), e.getMoTa(), e.getTrangThai());
    }
    @Override public List<AttributeMoTaDto> findAll() { return repo.findAll().stream().map(this::toDto).toList(); }
    @Override public AttributeMoTaDto findById(int id) { return repo.findById(id).map(this::toDto).orElse(null); }

    @Override @Transactional
    public AttributeMoTaDto create(AttributeMoTaDto dto) {
        ThuongHieu e = new ThuongHieu();
        e.setTenThuongHieu(dto.getTen());
        e.setMoTa(dto.getMoTa());
        e.setTrangThai(dto.getTrangThai() == null ? Boolean.TRUE : dto.getTrangThai());
        e.setTrangThaiXoa(false);
        e = repo.save(e);
        e.setMaThuongHieu("TH" + e.getId());
        return toDto(repo.save(e));
    }
    @Override @Transactional
    public AttributeMoTaDto update(AttributeMoTaDto dto) {
        ThuongHieu e = repo.findById(dto.getId()).orElseThrow();
        e.setTenThuongHieu(dto.getTen());
        e.setMoTa(dto.getMoTa());
        if (dto.getTrangThai() != null) e.setTrangThai(dto.getTrangThai());
        return toDto(repo.save(e));
    }
    @Override @Transactional public void delete(int id) { repo.deleteById(id); }
}
