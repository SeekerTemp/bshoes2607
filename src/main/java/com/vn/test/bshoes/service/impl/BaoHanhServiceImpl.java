package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.BaoHanhDto;
import com.vn.test.bshoes.entity.BaoHanh;
import com.vn.test.bshoes.entity.SanPhamChiTiet;
import com.vn.test.bshoes.repository.BaoHanhRepository;
import com.vn.test.bshoes.repository.HoaDonRepository;
import com.vn.test.bshoes.repository.KhachHangRepository;
import com.vn.test.bshoes.repository.NhanVienRepository;
import com.vn.test.bshoes.repository.SanPhamChiTietRepository;
import com.vn.test.bshoes.service.BaoHanhService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class BaoHanhServiceImpl implements BaoHanhService {

    private final BaoHanhRepository baoHanhRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final KhachHangRepository khachHangRepository;
    private final HoaDonRepository hoaDonRepository;
    private final NhanVienRepository nhanVienRepository;

    public BaoHanhServiceImpl(BaoHanhRepository baoHanhRepository,
                              SanPhamChiTietRepository sanPhamChiTietRepository,
                              KhachHangRepository khachHangRepository,
                              HoaDonRepository hoaDonRepository,
                              NhanVienRepository nhanVienRepository) {
        this.baoHanhRepository = baoHanhRepository;
        this.sanPhamChiTietRepository = sanPhamChiTietRepository;
        this.khachHangRepository = khachHangRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    private String ds(Instant i) { return i != null ? i.toString() : null; }

    /** Derive the UI tab bucket from status + expiry. */
    private String cat(BaoHanh b) {
        String s = b.getTrangThai() == null ? "" : b.getTrangThai();
        if (b.getNgayKetThuc() != null && b.getNgayKetThuc().isBefore(Instant.now())
                && !s.equals("Đã xử lý")) return "expired";
        return switch (s) {
            case "Chưa xử lý" -> "wait";
            case "Đã chẩn đoán", "Đang xử lý" -> "pickup";
            case "Đã xử lý", "Đã trả" -> "success";
            case "Đã thu phí" -> "fail";
            default -> "wait";
        };
    }

    private BaoHanhDto toDto(BaoHanh b) {
        BaoHanhDto d = new BaoHanhDto();
        d.setId(b.getId());
        d.setMa(b.getMaBaoHanh());
        SanPhamChiTiet spct = b.getIdSanPhamChiTiet();
        if (spct != null) {
            d.setIdSanPhamChiTiet(spct.getId());
            d.setModel(spct.getIdSanPham() != null ? spct.getIdSanPham().getTenSanPham() : null);
            String mau = spct.getIdMauSac() != null ? spct.getIdMauSac().getTenMauSac() : "";
            String size = spct.getIdKichCo() != null ? spct.getIdKichCo().getTenKichCo() : "";
            d.setMauSize((mau + " / " + size).trim());
            d.setImageUrl(spct.getImageUrl());
        }
        d.setSerial(b.getSerial());
        if (b.getIdKhachHang() != null) {
            d.setIdKhachHang(b.getIdKhachHang().getId());
            d.setMaKH(b.getIdKhachHang().getMaKhachHang());
            d.setTenKH(b.getIdKhachHang().getTenKhachHang());
            d.setSdt(b.getIdKhachHang().getSoDienThoai());
        }
        if (b.getIdHoaDon() != null) {
            d.setIdHoaDon(b.getIdHoaDon().getId());
            d.setMaHD(b.getIdHoaDon().getMaHoaDon());
        }
        if (b.getIdNhanVien() != null) d.setNv(b.getIdNhanVien().getTenNhanVien());
        d.setDonVi(b.getDonViBaoHanh());
        d.setLoai(b.getLoaiYeuCau());
        d.setMoTa(b.getMoTaLoi());
        d.setChiPhi(b.getChiPhi());
        d.setThayLinhKien(b.getThayLinhKien());
        d.setBatDau(ds(b.getNgayBatDau()));
        d.setHetHan(ds(b.getNgayKetThuc()));
        d.setTrangThai(b.getTrangThai());
        d.setCat(cat(b));
        return d;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BaoHanhDto> findAll() {
        return baoHanhRepository.findActive().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BaoHanhDto findById(int id) {
        return baoHanhRepository.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    @Transactional
    public BaoHanhDto create(BaoHanhDto dto) {
        BaoHanh b = new BaoHanh();
        if (dto.getIdSanPhamChiTiet() != null)
            sanPhamChiTietRepository.findById(dto.getIdSanPhamChiTiet()).ifPresent(b::setIdSanPhamChiTiet);
        if (dto.getIdKhachHang() != null)
            khachHangRepository.findById(dto.getIdKhachHang()).ifPresent(b::setIdKhachHang);
        if (dto.getIdHoaDon() != null)
            hoaDonRepository.findById(dto.getIdHoaDon()).ifPresent(b::setIdHoaDon);
        if (dto.getIdNhanVien() != null)
            nhanVienRepository.findById(dto.getIdNhanVien()).ifPresent(b::setIdNhanVien);
        b.setSerial(dto.getSerial());
        b.setMoTaLoi(dto.getMoTa());
        b.setLoaiYeuCau(dto.getLoai());
        b.setDonViBaoHanh(dto.getDonVi());
        b.setChiPhi(dto.getChiPhi());
        b.setThayLinhKien(dto.getThayLinhKien() != null && dto.getThayLinhKien());
        Instant now = Instant.now();
        b.setNgayBatDau(now);
        b.setNgayKetThuc(now.plusSeconds(60L * 60 * 24 * 180)); // +180 ngày mặc định
        b.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : "Chưa xử lý");
        b.setNgayTao(now);
        b.setNgayCapNhat(now);
        b.setTrangThaiXoa(false);
        b = baoHanhRepository.save(b);
        b.setMaBaoHanh("BH" + String.format("%04d", b.getId()));
        return toDto(baoHanhRepository.save(b));
    }

    @Override
    @Transactional
    public BaoHanhDto updateTrangThai(int id, String trangThai) {
        BaoHanh b = baoHanhRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Đơn bảo hành không tồn tại."));
        b.setTrangThai(trangThai);
        b.setNgayCapNhat(Instant.now());
        return toDto(baoHanhRepository.save(b));
    }

    @Override
    @Transactional
    public void softDelete(int id) {
        baoHanhRepository.findById(id).ifPresent(b -> {
            b.setTrangThaiXoa(true);
            b.setNgayCapNhat(Instant.now());
            baoHanhRepository.save(b);
        });
    }
}
