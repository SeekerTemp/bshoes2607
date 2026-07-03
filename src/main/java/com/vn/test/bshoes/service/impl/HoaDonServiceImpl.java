package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.HoaDonChiTietDto;
import com.vn.test.bshoes.dto.HoaDonDto;
import com.vn.test.bshoes.dto.PhieuGiamGiaDto;
import com.vn.test.bshoes.dto.PosSanPhamDto;
import com.vn.test.bshoes.entity.HoaDon;
import com.vn.test.bshoes.entity.HoaDonChiTiet;
import com.vn.test.bshoes.entity.PhieuGiamGia;
import com.vn.test.bshoes.entity.SanPhamChiTiet;
import com.vn.test.bshoes.repository.HoaDonChiTietRepository;
import com.vn.test.bshoes.repository.HoaDonRepository;
import com.vn.test.bshoes.repository.SanPhamChiTietRepository;
import com.vn.test.bshoes.service.HoaDonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class HoaDonServiceImpl implements HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;

    public HoaDonServiceImpl(HoaDonRepository hoaDonRepository,
                             HoaDonChiTietRepository hoaDonChiTietRepository,
                             SanPhamChiTietRepository sanPhamChiTietRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.hoaDonChiTietRepository = hoaDonChiTietRepository;
        this.sanPhamChiTietRepository = sanPhamChiTietRepository;
    }

    private String trangThaiLabel(Integer trangThai) {
        if (trangThai == null) return "Khác";
        return switch (trangThai) {
            case 0 -> "Chờ";
            case 1 -> "Thành công";
            case 2 -> "Huỷ";
            default -> "Khác";
        };
    }

    private HoaDonChiTietDto chiTietToDto(HoaDonChiTiet h) {
        SanPhamChiTiet spct = h.getIdSanPhamChiTiet();
        HoaDonChiTietDto dto = new HoaDonChiTietDto();
        dto.setId(h.getId());
        dto.setTen(spct != null && spct.getIdSanPham() != null ? spct.getIdSanPham().getTenSanPham() : null);
        dto.setSoLuong(h.getSoLuong());
        dto.setDonGia(spct != null ? spct.getDonGia() : null);
        dto.setThanhTien(h.getThanhTien());
        return dto;
    }

    private HoaDonDto toDto(HoaDon h) {
        HoaDonDto dto = new HoaDonDto();
        dto.setId(h.getId());
        dto.setMa(h.getMaHoaDon());
        dto.setKhach(h.getIdKhachHang() != null ? h.getIdKhachHang().getTenKhachHang() : h.getTenNguoiNhan());
        dto.setNhanVien(h.getIdNhanVien() != null ? h.getIdNhanVien().getTenNhanVien() : null);
        dto.setNgayTao(h.getNgayTaoMa() != null ? h.getNgayTaoMa().toString() : null);
        dto.setTongTien(h.getTongTienPhaiTra());
        dto.setTrangThai(trangThaiLabel(h.getTrangThai()));
        List<HoaDonChiTietDto> chiTiet = hoaDonChiTietRepository.findByHoaDon(h.getId()).stream()
                .map(this::chiTietToDto).toList();
        dto.setChiTiet(chiTiet);
        return dto;
    }

    private PhieuGiamGiaDto voucherToDto(PhieuGiamGia p) {
        PhieuGiamGiaDto dto = new PhieuGiamGiaDto();
        dto.setId(p.getId());
        dto.setMa(p.getMaPhieuGiam());
        dto.setTen(p.getTenPhieuGiam());
        dto.setLoai(p.getLoaiGiamGia());
        dto.setGiaTri(p.getGiaTriGiam());
        dto.setDonToiThieu(p.getDonToiThieu());
        dto.setGiamToiDa(p.getGiamToiDa());
        dto.setSoLuong(p.getSoLuong());
        dto.setBatDau(p.getThoiGianBatDau() != null ? p.getThoiGianBatDau().toString() : null);
        dto.setKetThuc(p.getThoiGianKetThuc() != null ? p.getThoiGianKetThuc().toString() : null);
        dto.setTrangThai(p.getTrangThai());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDonDto> findAll() {
        return hoaDonRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HoaDonDto findById(int id) {
        return hoaDonRepository.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDonDto> findCart() {
        return hoaDonRepository.findByTrangThai(0).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PosSanPhamDto> posProducts() {
        return sanPhamChiTietRepository.findAvailable().stream()
                .map(s -> {
                    PosSanPhamDto dto = new PosSanPhamDto();
                    dto.setId(s.getId());
                    dto.setTen(s.getIdSanPham() != null ? s.getIdSanPham().getTenSanPham() : null);
                    dto.setMau(s.getIdMauSac() != null ? s.getIdMauSac().getTenMauSac() : null);
                    dto.setSize(s.getIdKichCo() != null ? s.getIdKichCo().getTenKichCo() : null);
                    dto.setTon(s.getSoLuongTon());
                    dto.setGia(s.getDonGia());
                    dto.setImageUrl(s.getImageUrl());
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhieuGiamGiaDto> vouchersActive() {
        return hoaDonRepository.getVouchersActive().stream()
                .map(this::voucherToDto)
                .toList();
    }

    @Override
    public BigDecimal tinhGiamGia(int idPhieu, BigDecimal tongTien) {
        return hoaDonRepository.tinhGiamGia(idPhieu, tongTien);
    }

    @Override
    @Transactional
    public HoaDonDto create(HoaDonDto dto) {
        HoaDon h = new HoaDon();
        h.setTrangThai(0);
        h.setTongTienPhaiTra(dto.getTongTien());
        h.setNgayTaoMa(Instant.now());
        h = hoaDonRepository.save(h);
        h.setMaHoaDon("HD" + h.getId());
        return toDto(hoaDonRepository.save(h));
    }
}
