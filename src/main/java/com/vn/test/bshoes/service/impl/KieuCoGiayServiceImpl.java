package com.vn.test.bshoes.service.impl;
import com.vn.test.bshoes.dto.AttributeDto;
import com.vn.test.bshoes.entity.KieuCoGiay;
import com.vn.test.bshoes.repository.KieuCoGiayRepository;
import com.vn.test.bshoes.service.KieuCoGiayService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class KieuCoGiayServiceImpl implements KieuCoGiayService {
    private final KieuCoGiayRepository repo;
    public KieuCoGiayServiceImpl(KieuCoGiayRepository repo) { this.repo = repo; }

    private AttributeDto toDto(KieuCoGiay e) {
        return new AttributeDto(e.getId(), e.getMaCoGiay(), e.getTenCoGiay(), e.getTrangThai());
    }
    @Override public List<AttributeDto> findAll() { return repo.findAll().stream().map(this::toDto).toList(); }
    @Override public AttributeDto findById(int id) { return repo.findById(id).map(this::toDto).orElse(null); }

    @Override @Transactional
    public AttributeDto create(AttributeDto dto) {
        KieuCoGiay e = new KieuCoGiay();
        e.setTenCoGiay(dto.getTen());
        e.setTrangThai(dto.getTrangThai() == null ? Boolean.TRUE : dto.getTrangThai());
        e.setTrangThaiXoa(false);
        e = repo.save(e);
        e.setMaCoGiay("KC" + e.getId());
        return toDto(repo.save(e));
    }
    @Override @Transactional
    public AttributeDto update(AttributeDto dto) {
        KieuCoGiay e = repo.findById(dto.getId()).orElseThrow();
        e.setTenCoGiay(dto.getTen());
        if (dto.getTrangThai() != null) e.setTrangThai(dto.getTrangThai());
        return toDto(repo.save(e));
    }
    @Override @Transactional public void delete(int id) { repo.deleteById(id); }
}
