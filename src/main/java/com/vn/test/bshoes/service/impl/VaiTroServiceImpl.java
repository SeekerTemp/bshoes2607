package com.vn.test.bshoes.service.impl;
import com.vn.test.bshoes.dto.AttributeDto;
import com.vn.test.bshoes.dto.PhanQuyenDto;
import com.vn.test.bshoes.entity.VaiTro;
import com.vn.test.bshoes.repository.VaiTroRepository;
import com.vn.test.bshoes.service.VaiTroService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class VaiTroServiceImpl implements VaiTroService {
    private final VaiTroRepository repo;
    public VaiTroServiceImpl(VaiTroRepository repo) { this.repo = repo; }

    private AttributeDto toDto(VaiTro e) {
        return new AttributeDto(e.getId(), e.getMaVaiTro(), e.getTenVaiTro(), e.getTrangThai());
    }
    private PhanQuyenDto toQuyenDto(VaiTro e) {
        return new PhanQuyenDto(e.getId(), e.getMaVaiTro(), e.getTenVaiTro(), e.getQuyen());
    }
    @Override public List<AttributeDto> findAll() { return repo.findAll().stream().map(this::toDto).toList(); }
    @Override public AttributeDto findById(int id) { return repo.findById(id).map(this::toDto).orElse(null); }

    @Override
    public List<PhanQuyenDto> findAllQuyen() { return repo.findAll().stream().map(this::toQuyenDto).toList(); }

    @Override
    @Transactional
    public PhanQuyenDto updateQuyen(int id, String quyen) {
        VaiTro e = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Vai trò không tồn tại."));
        e.setQuyen(quyen);
        return toQuyenDto(repo.save(e));
    }
}
