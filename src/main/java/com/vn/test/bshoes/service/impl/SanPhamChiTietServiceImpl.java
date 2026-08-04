package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.BienTheDto;
import com.vn.test.bshoes.dto.PosSanPhamDto;
import com.vn.test.bshoes.entity.SanPham;
import com.vn.test.bshoes.entity.SanPhamChiTiet;
import com.vn.test.bshoes.repository.KichCoRepository;
import com.vn.test.bshoes.repository.MauSacRepository;
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
    private final MauSacRepository mauSacRepository;
    private final KichCoRepository kichCoRepository;

    public SanPhamChiTietServiceImpl(SanPhamChiTietRepository repo, SanPhamRepository sanPhamRepository,
                                     MauSacRepository mauSacRepository, KichCoRepository kichCoRepository) {
        this.repo = repo;
        this.sanPhamRepository = sanPhamRepository;
        this.mauSacRepository = mauSacRepository;
        this.kichCoRepository = kichCoRepository;
    }

    private BienTheDto toDto(SanPhamChiTiet v) {
        BienTheDto dto = new BienTheDto();
        dto.setId(v.getId());
        dto.setMa(v.getMaSanPhamChiTiet());
        dto.setMau(v.getIdMauSac() != null ? v.getIdMauSac().getTenMauSac() : null);
        dto.setSize(v.getIdKichCo() != null ? v.getIdKichCo().getTenKichCo() : null);
        dto.setTon(v.getSoLuongTon());
        dto.setGia(v.getDonGia());
        dto.setGiaNhap(v.getGiaNhap());
        dto.setImageUrl(v.getImageUrl());
        dto.setTrangThai(v.getTrangThai());
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
        v.setGiaNhap(dto.getGiaNhap());
        if (StringUtils.hasText(dto.getMau())) v.setIdMauSac(mauSacRepository.findByTenMauSac(dto.getMau()));
        if (StringUtils.hasText(dto.getSize())) v.setIdKichCo(kichCoRepository.findByTenKichCo(dto.getSize()));
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
    public BienTheDto update(int id, BienTheDto dto) {
        SanPhamChiTiet v = repo.findById(id).orElseThrow();
        if (dto.getTon() != null) v.setSoLuongTon(dto.getTon());
        if (dto.getGia() != null) v.setDonGia(dto.getGia());
        if (dto.getGiaNhap() != null) v.setGiaNhap(dto.getGiaNhap());
        if (dto.getTrangThai() != null) v.setTrangThai(dto.getTrangThai());
        if (StringUtils.hasText(dto.getMau())) v.setIdMauSac(mauSacRepository.findByTenMauSac(dto.getMau()));
        if (StringUtils.hasText(dto.getSize())) v.setIdKichCo(kichCoRepository.findByTenKichCo(dto.getSize()));
        if (StringUtils.hasText(dto.getMa())) v.setMaSanPhamChiTiet(dto.getMa());
        if (StringUtils.hasText(dto.getImageUrl())) v.setImageUrl(dto.getImageUrl());
        return toDto(repo.save(v));
    }

    @Override
    @Transactional
    public BienTheDto nhapKho(int id, int soLuong) {
        SanPhamChiTiet v = repo.findById(id).orElseThrow();
        int cur = v.getSoLuongTon() == null ? 0 : v.getSoLuongTon();
        v.setSoLuongTon(cur + Math.max(0, soLuong));
        if (v.getSoLuongTon() > 0) v.setTrangThai(true);
        return toDto(repo.save(v));
    }

    @Override
    @Transactional
    public void softDelete(int id) {
        SanPhamChiTiet v = repo.findById(id).orElseThrow();
        v.setTrangThaiXoa(true);
        repo.save(v);
    }

    private PosSanPhamDto toPos(SanPhamChiTiet v) {
        PosSanPhamDto dto = new PosSanPhamDto();
        dto.setId(v.getId());
        dto.setMa(v.getMaSanPhamChiTiet());
        dto.setIdSanPham(v.getIdSanPham() != null ? v.getIdSanPham().getId() : null);
        dto.setTen(v.getIdSanPham() != null ? v.getIdSanPham().getTenSanPham() : null);
        dto.setMau(v.getIdMauSac() != null ? v.getIdMauSac().getTenMauSac() : null);
        dto.setSize(v.getIdKichCo() != null ? v.getIdKichCo().getTenKichCo() : null);
        dto.setTon(v.getSoLuongTon());
        dto.setGia(v.getDonGia());
        dto.setImageUrl(v.getImageUrl());
        if (v.getIdSanPham() != null && v.getIdSanPham().getIdLoaiSanPham() != null) {
            dto.setIdLoaiSanPham(v.getIdSanPham().getIdLoaiSanPham().getId());
            dto.setLoaiSP(v.getIdSanPham().getIdLoaiSanPham().getTenLoaiSanPham());
        }
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PosSanPhamDto> storeProducts() {
        return repo.findForStore().stream().map(this::toPos).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PosSanPhamDto findPosByMa(String ma) {
        SanPhamChiTiet v = repo.findByMaSanPhamChiTiet(ma);
        return v == null ? null : toPos(v);
    }
}
