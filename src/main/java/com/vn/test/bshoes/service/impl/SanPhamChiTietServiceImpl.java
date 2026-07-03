package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.BienTheDto;
import com.vn.test.bshoes.entity.SanPham;
import com.vn.test.bshoes.entity.SanPhamChiTiet;
import com.vn.test.bshoes.repository.SanPhamChiTietRepository;
import com.vn.test.bshoes.repository.SanPhamRepository;
import com.vn.test.bshoes.service.SanPhamChiTietService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SanPhamChiTietServiceImpl implements SanPhamChiTietService {

    private final SanPhamChiTietRepository repo;
    private final SanPhamRepository sanPhamRepository;

    public SanPhamChiTietServiceImpl(SanPhamChiTietRepository repo, SanPhamRepository sanPhamRepository) {
        this.repo = repo;
        this.sanPhamRepository = sanPhamRepository;
    }

    private BienTheDto toDto(SanPhamChiTiet v) {
        BienTheDto dto = new BienTheDto();
        dto.setId(v.getId());
        dto.setMa(v.getMaSanPhamChiTiet());
        dto.setMau(v.getIdMauSac() != null ? v.getIdMauSac().getTenMauSac() : null);
        dto.setSize(v.getIdKichCo() != null ? v.getIdKichCo().getTenKichCo() : null);
        dto.setTon(v.getSoLuongTon());
        dto.setGia(v.getDonGia());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BienTheDto> findActive() {
        return repo.findActive().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BienTheDto> findByProduct(int idSanPham) {
        return repo.findActiveByProduct(idSanPham).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BienTheDto> findAvailable() {
        return repo.findAvailable().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BienTheDto findById(int id) {
        return repo.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    @Transactional
    public BienTheDto create(int idSanPham, BienTheDto dto) {
        SanPhamChiTiet v = new SanPhamChiTiet();
        v.setIdSanPham(sanPhamRepository.getReferenceById(idSanPham));
        v.setSoLuongTon(dto.getTon());
        v.setDonGia(dto.getGia());
        if (StringUtils.hasText(dto.getMa())) {
            v.setMaSanPhamChiTiet(dto.getMa());
        } else {
            SanPham parent = sanPhamRepository.findById(idSanPham).orElse(null);
            String parentMa = parent != null ? parent.getMaSanPham() : "";
            v.setMaSanPhamChiTiet("SPCT" + parentMa);
        }
        v.setTrangThai(true);
        v.setTrangThaiXoa(false);
        return toDto(repo.save(v));
    }

    @Override
    @Transactional
    public void softDelete(int id) {
        SanPhamChiTiet v = repo.findById(id).orElseThrow();
        v.setTrangThaiXoa(true);
        repo.save(v);
    }
}
