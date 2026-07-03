package com.vn.test.bshoes.service.impl;
import com.vn.test.bshoes.dto.AttributeDto;
import com.vn.test.bshoes.entity.KieuDayGiay;
import com.vn.test.bshoes.repository.KieuDayGiayRepository;
import com.vn.test.bshoes.service.KieuDayGiayService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class KieuDayGiayServiceImpl implements KieuDayGiayService {
    private final KieuDayGiayRepository repo;
    public KieuDayGiayServiceImpl(KieuDayGiayRepository repo) { this.repo = repo; }

    private AttributeDto toDto(KieuDayGiay e) {
        return new AttributeDto(e.getId(), e.getMaDayGiay(), e.getTenDayGiay(), e.getTrangThai());
    }
    @Override public List<AttributeDto> findAll() { return repo.findAll().stream().map(this::toDto).toList(); }
    @Override public AttributeDto findById(int id) { return repo.findById(id).map(this::toDto).orElse(null); }

    @Override @Transactional
    public AttributeDto create(AttributeDto dto) {
        KieuDayGiay e = new KieuDayGiay();
        e.setTenDayGiay(dto.getTen());
        e.setTrangThai(dto.getTrangThai() == null ? Boolean.TRUE : dto.getTrangThai());
        e.setTrangThaiXoa(false);
        e = repo.save(e);
        e.setMaDayGiay("DG" + e.getId());
        return toDto(repo.save(e));
    }
    @Override @Transactional
    public AttributeDto update(AttributeDto dto) {
        KieuDayGiay e = repo.findById(dto.getId()).orElseThrow();
        e.setTenDayGiay(dto.getTen());
        if (dto.getTrangThai() != null) e.setTrangThai(dto.getTrangThai());
        return toDto(repo.save(e));
    }
    @Override @Transactional public void delete(int id) { repo.deleteById(id); }
}
