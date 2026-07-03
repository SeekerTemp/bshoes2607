package com.vn.test.bshoes.service.impl;
import com.vn.test.bshoes.dto.AttributeMoTaDto;
import com.vn.test.bshoes.entity.XuatSu;
import com.vn.test.bshoes.repository.XuatSuRepository;
import com.vn.test.bshoes.service.XuatSuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class XuatSuServiceImpl implements XuatSuService {
    private final XuatSuRepository repo;
    public XuatSuServiceImpl(XuatSuRepository repo) { this.repo = repo; }

    private AttributeMoTaDto toDto(XuatSu e) {
        return new AttributeMoTaDto(e.getId(), e.getMaXuatSu(), e.getTenXuatSu(), e.getMoTa(), e.getTrangThai());
    }
    @Override public List<AttributeMoTaDto> findAll() { return repo.findAll().stream().map(this::toDto).toList(); }
    @Override public AttributeMoTaDto findById(int id) { return repo.findById(id).map(this::toDto).orElse(null); }

    @Override @Transactional
    public AttributeMoTaDto create(AttributeMoTaDto dto) {
        XuatSu e = new XuatSu();
        e.setTenXuatSu(dto.getTen());
        e.setMoTa(dto.getMoTa());
        e.setTrangThai(dto.getTrangThai() == null ? Boolean.TRUE : dto.getTrangThai());
        e.setTrangThaiXoa(false);
        e = repo.save(e);
        e.setMaXuatSu("XX" + e.getId());
        return toDto(repo.save(e));
    }
    @Override @Transactional
    public AttributeMoTaDto update(AttributeMoTaDto dto) {
        XuatSu e = repo.findById(dto.getId()).orElseThrow();
        e.setTenXuatSu(dto.getTen());
        e.setMoTa(dto.getMoTa());
        if (dto.getTrangThai() != null) e.setTrangThai(dto.getTrangThai());
        return toDto(repo.save(e));
    }
    @Override @Transactional public void delete(int id) { repo.deleteById(id); }
}
