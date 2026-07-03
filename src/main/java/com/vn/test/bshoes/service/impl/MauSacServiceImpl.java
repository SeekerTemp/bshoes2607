package com.vn.test.bshoes.service.impl;
import com.vn.test.bshoes.dto.AttributeDto;
import com.vn.test.bshoes.entity.MauSac;
import com.vn.test.bshoes.repository.MauSacRepository;
import com.vn.test.bshoes.service.MauSacService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class MauSacServiceImpl implements MauSacService {
    private final MauSacRepository repo;
    public MauSacServiceImpl(MauSacRepository repo) { this.repo = repo; }

    private AttributeDto toDto(MauSac e) {
        return new AttributeDto(e.getId(), e.getMaMauSac(), e.getTenMauSac(), e.getTrangThai());
    }
    @Override public List<AttributeDto> findAll() { return repo.findAll().stream().map(this::toDto).toList(); }
    @Override public AttributeDto findById(int id) { return repo.findById(id).map(this::toDto).orElse(null); }

    @Override @Transactional
    public AttributeDto create(AttributeDto dto) {
        MauSac e = new MauSac();
        e.setMaMauSac(dto.getMa());
        e.setTenMauSac(dto.getTen());
        e.setTrangThai(dto.getTrangThai() == null ? Boolean.TRUE : dto.getTrangThai());
        e.setTrangThaiXoa(false);
        return toDto(repo.save(e));
    }
    @Override @Transactional
    public AttributeDto update(AttributeDto dto) {
        MauSac e = repo.findById(dto.getId()).orElseThrow();
        e.setTenMauSac(dto.getTen());
        if (dto.getTrangThai() != null) e.setTrangThai(dto.getTrangThai());
        return toDto(repo.save(e));
    }
    @Override @Transactional public void delete(int id) { repo.deleteById(id); }
}
