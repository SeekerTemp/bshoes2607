package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.HoaDonChiTietDto;
import com.vn.test.bshoes.entity.HoaDonChiTiet;
import com.vn.test.bshoes.entity.SanPhamChiTiet;
import com.vn.test.bshoes.repository.HoaDonChiTietRepository;
import com.vn.test.bshoes.service.HoaDonChiTietService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HoaDonChiTietServiceImpl implements HoaDonChiTietService {

    private final HoaDonChiTietRepository hoaDonChiTietRepository;

    public HoaDonChiTietServiceImpl(HoaDonChiTietRepository hoaDonChiTietRepository) {
        this.hoaDonChiTietRepository = hoaDonChiTietRepository;
    }

    private HoaDonChiTietDto toDto(HoaDonChiTiet h) {
        SanPhamChiTiet spct = h.getIdSanPhamChiTiet();
        HoaDonChiTietDto dto = new HoaDonChiTietDto();
        dto.setId(h.getId());
        dto.setTen(spct != null && spct.getIdSanPham() != null ? spct.getIdSanPham().getTenSanPham() : null);
        dto.setSoLuong(h.getSoLuong());
        dto.setDonGia(spct != null ? spct.getDonGia() : null);
        dto.setThanhTien(h.getThanhTien());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDonChiTietDto> findByHoaDon(int idHoaDon) {
        return hoaDonChiTietRepository.findByHoaDon(idHoaDon).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HoaDonChiTietDto findById(int id) {
        return hoaDonChiTietRepository.findById(id).map(this::toDto).orElse(null);
    }
}
