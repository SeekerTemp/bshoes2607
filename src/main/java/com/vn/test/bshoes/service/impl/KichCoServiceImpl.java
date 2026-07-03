package com.vn.test.bshoes.service.impl;
import com.vn.test.bshoes.dto.AttributeDto;
import com.vn.test.bshoes.entity.KichCo;
import com.vn.test.bshoes.repository.KichCoRepository;
import com.vn.test.bshoes.service.KichCoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class KichCoServiceImpl implements KichCoService {
    private final KichCoRepository repo;
    public KichCoServiceImpl(KichCoRepository repo) { this.repo = repo; }

    private AttributeDto toDto(KichCo e) {
        return new AttributeDto(e.getId(), e.getMaKichCo(), e.getTenKichCo(), e.getTrangThai());
    }
    @Override public List<AttributeDto> findAll() { return repo.findAll().stream().map(this::toDto).toList(); }
    @Override public AttributeDto findById(int id) { return repo.findById(id).map(this::toDto).orElse(null); }

    @Override @Transactional
    public AttributeDto create(AttributeDto dto) {
        KichCo e = new KichCo();
        e.setMaKichCo(dto.getMa());
        e.setTenKichCo(dto.getTen());
        e.setTrangThai(dto.getTrangThai() == null ? Boolean.TRUE : dto.getTrangThai());
        e.setTrangThaiXoa(false);
        return toDto(repo.save(e));
    }
    @Override @Transactional
    public AttributeDto update(AttributeDto dto) {
        KichCo e = repo.findById(dto.getId()).orElseThrow();
        e.setTenKichCo(dto.getTen());
        if (dto.getTrangThai() != null) e.setTrangThai(dto.getTrangThai());
        return toDto(repo.save(e));
    }
    @Override @Transactional public void delete(int id) { repo.deleteById(id); }
}
