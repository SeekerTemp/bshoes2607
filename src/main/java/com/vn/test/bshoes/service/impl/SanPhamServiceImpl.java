package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.BienTheDto;
import com.vn.test.bshoes.dto.SanPhamDto;
import com.vn.test.bshoes.entity.SanPham;
import com.vn.test.bshoes.repository.ChatLieuRepository;
import com.vn.test.bshoes.repository.LoaiSanPhamRepository;
import com.vn.test.bshoes.repository.SanPhamChiTietRepository;
import com.vn.test.bshoes.repository.SanPhamRepository;
import com.vn.test.bshoes.repository.ThuongHieuRepository;
import com.vn.test.bshoes.service.SanPhamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SanPhamServiceImpl implements SanPhamService {

    private final SanPhamRepository repo;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final ThuongHieuRepository thuongHieuRepository;
    private final ChatLieuRepository chatLieuRepository;
    private final LoaiSanPhamRepository loaiSanPhamRepository;

    public SanPhamServiceImpl(SanPhamRepository repo,
                               SanPhamChiTietRepository sanPhamChiTietRepository,
                               ThuongHieuRepository thuongHieuRepository,
                               ChatLieuRepository chatLieuRepository,
                               LoaiSanPhamRepository loaiSanPhamRepository) {
        this.repo = repo;
        this.sanPhamChiTietRepository = sanPhamChiTietRepository;
        this.thuongHieuRepository = thuongHieuRepository;
        this.chatLieuRepository = chatLieuRepository;
        this.loaiSanPhamRepository = loaiSanPhamRepository;
    }

    private SanPhamDto toDto(SanPham s) {
        List<BienTheDto> bienThe = sanPhamChiTietRepository.findActiveByProduct(s.getId()).stream()
                .map(v -> {
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
                })
                .toList();

        SanPhamDto dto = new SanPhamDto();
        dto.setId(s.getId());
        dto.setMa(s.getMaSanPham());
        dto.setTen(s.getTenSanPham());
        dto.setIdLoaiSanPham(s.getIdLoaiSanPham() != null ? s.getIdLoaiSanPham().getId() : null);
        dto.setLoaiSP(s.getIdLoaiSanPham() != null ? s.getIdLoaiSanPham().getTenLoaiSanPham() : null);
        dto.setThuongHieu(s.getIdThuongHieu() != null ? s.getIdThuongHieu().getTenThuongHieu() : null);
        dto.setKieuDang(s.getIdKieuDang() != null ? s.getIdKieuDang().getTenKieuDang() : null);
        dto.setChatLieu(s.getIdChatLieu() != null ? s.getIdChatLieu().getTenChatLieu() : null);
        dto.setGia(!bienThe.isEmpty() ? bienThe.get(0).getGia() : null);
        dto.setImageUrl(!bienThe.isEmpty() ? bienThe.get(0).getImageUrl() : null);
        dto.setMoTa(s.getMoTa());
        dto.setBienThe(bienThe);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SanPhamDto> findAllActive() {
        return repo.findActive().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SanPhamDto> findRecycle() {
        return repo.findRecycle().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SanPhamDto findById(int id) {
        return repo.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SanPhamDto> search(String keyword) {
        return repo.search(keyword).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public SanPhamDto create(SanPhamDto dto) {
        SanPham s = new SanPham();
        s.setTenSanPham(dto.getTen());
        s.setMoTa(dto.getMoTa());
        s.setTrangThai(true);
        s.setTrangThaiXoa(false);
        if (StringUtils.hasText(dto.getThuongHieu())) {
            s.setIdThuongHieu(thuongHieuRepository.findByTenThuongHieu(dto.getThuongHieu()));
        }
        if (StringUtils.hasText(dto.getChatLieu())) {
            s.setIdChatLieu(chatLieuRepository.findByTenChatLieu(dto.getChatLieu()));
        }
        if (dto.getIdLoaiSanPham() != null) {
            s.setIdLoaiSanPham(loaiSanPhamRepository.findById(dto.getIdLoaiSanPham()).orElse(null));
        }
        s = repo.save(s);
        s.setMaSanPham("SP" + s.getId());
        return toDto(repo.save(s));
    }

    @Override
    @Transactional
    public SanPhamDto update(SanPhamDto dto) {
        SanPham s = repo.findById(dto.getId()).orElseThrow();
        s.setTenSanPham(dto.getTen());
        s.setMoTa(dto.getMoTa());
        if (StringUtils.hasText(dto.getThuongHieu())) {
            s.setIdThuongHieu(thuongHieuRepository.findByTenThuongHieu(dto.getThuongHieu()));
        }
        if (StringUtils.hasText(dto.getChatLieu())) {
            s.setIdChatLieu(chatLieuRepository.findByTenChatLieu(dto.getChatLieu()));
        }
        if (dto.getIdLoaiSanPham() != null) {
            s.setIdLoaiSanPham(loaiSanPhamRepository.findById(dto.getIdLoaiSanPham()).orElse(null));
        }
        return toDto(repo.save(s));
    }

    @Override
    @Transactional
    public SanPhamDto setDanhMuc(int idSanPham, Integer idLoai) {
        SanPham s = repo.findById(idSanPham).orElseThrow();
        s.setIdLoaiSanPham(idLoai != null ? loaiSanPhamRepository.findById(idLoai).orElse(null) : null);
        return toDto(repo.save(s));
    }

    @Override
    @Transactional
    public void softDelete(String ma) {
        SanPham s = repo.findByMaSanPham(ma);
        if (s != null) {
            s.setTrangThaiXoa(true);
            repo.save(s);
        }
    }

    @Override
    @Transactional
    public void restore(String ma) {
        SanPham s = repo.findByMaSanPham(ma);
        if (s != null) {
            s.setTrangThaiXoa(false);
            repo.save(s);
        }
    }
}
