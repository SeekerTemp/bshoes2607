package com.vn.test.bshoes.service.impl;
import com.vn.test.bshoes.dto.AttributeDto;
import com.vn.test.bshoes.entity.KieuDang;
import com.vn.test.bshoes.repository.KieuDangRepository;
import com.vn.test.bshoes.service.KieuDangService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class KieuDangServiceImpl implements KieuDangService {
    private final KieuDangRepository repo;
    public KieuDangServiceImpl(KieuDangRepository repo) { this.repo = repo; }

    private AttributeDto toDto(KieuDang e) {
        return new AttributeDto(e.getId(), e.getMaKieuDang(), e.getTenKieuDang(), e.getTrangThai());
    }
    @Override public List<AttributeDto> findAll() { return repo.findAll().stream().map(this::toDto).toList(); }
    @Override public AttributeDto findById(int id) { return repo.findById(id).map(this::toDto).orElse(null); }

    @Override @Transactional
    public AttributeDto create(AttributeDto dto) {
        KieuDang e = new KieuDang();
        e.setTenKieuDang(dto.getTen());
        e.setTrangThai(dto.getTrangThai() == null ? Boolean.TRUE : dto.getTrangThai());
        e.setTrangThaiXoa(false);
        e = repo.save(e);
        e.setMaKieuDang("KD" + e.getId());
        return toDto(repo.save(e));
    }
    @Override @Transactional
    public AttributeDto update(AttributeDto dto) {
        KieuDang e = repo.findById(dto.getId()).orElseThrow();
        e.setTenKieuDang(dto.getTen());
        if (dto.getTrangThai() != null) e.setTrangThai(dto.getTrangThai());
        return toDto(repo.save(e));
    }
    @Override @Transactional public void delete(int id) { repo.deleteById(id); }
}
