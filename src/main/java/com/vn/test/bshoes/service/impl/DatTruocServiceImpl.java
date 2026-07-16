package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.AddItemRequest;
import com.vn.test.bshoes.dto.DatTruocDto;
import com.vn.test.bshoes.dto.DatTruocRequest;
import com.vn.test.bshoes.dto.HoaDonDto;
import com.vn.test.bshoes.entity.DatTruoc;
import com.vn.test.bshoes.entity.SanPhamChiTiet;
import com.vn.test.bshoes.repository.DatTruocRepository;
import com.vn.test.bshoes.repository.HoaDonRepository;
import com.vn.test.bshoes.repository.KhachHangRepository;
import com.vn.test.bshoes.repository.SanPhamChiTietRepository;
import com.vn.test.bshoes.service.DatTruocService;
import com.vn.test.bshoes.service.HoaDonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class DatTruocServiceImpl implements DatTruocService {

    public static final String CHO_HANG = "Chờ hàng";
    public static final String DA_CO_HANG = "Đã có hàng";
    public static final String DA_CHUYEN_DON = "Đã chuyển đơn";
    public static final String DA_HUY = "Đã hủy";

    private final DatTruocRepository repo;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final KhachHangRepository khachHangRepository;
    private final HoaDonRepository hoaDonRepository;
    private final HoaDonService hoaDonService;

    public DatTruocServiceImpl(DatTruocRepository repo,
                               SanPhamChiTietRepository sanPhamChiTietRepository,
                               KhachHangRepository khachHangRepository,
                               HoaDonRepository hoaDonRepository,
                               HoaDonService hoaDonService) {
        this.repo = repo;
        this.sanPhamChiTietRepository = sanPhamChiTietRepository;
        this.khachHangRepository = khachHangRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.hoaDonService = hoaDonService;
    }

    private String ds(Instant i) { return i != null ? i.toString() : null; }

    private DatTruocDto toDto(DatTruoc d) {
        DatTruocDto x = new DatTruocDto();
        x.setId(d.getId());
        x.setMa(d.getMaDatTruoc());
        SanPhamChiTiet v = d.getIdSanPhamChiTiet();
        if (v != null) {
            x.setIdSanPhamChiTiet(v.getId());
            x.setTenSanPham(v.getIdSanPham() != null ? v.getIdSanPham().getTenSanPham() : null);
            String mau = v.getIdMauSac() != null ? v.getIdMauSac().getTenMauSac() : "";
            String size = v.getIdKichCo() != null ? v.getIdKichCo().getTenKichCo() : "";
            x.setMauSize((mau + " / " + size).trim());
            x.setImageUrl(v.getImageUrl());
            x.setGia(v.getDonGia());
            x.setTon(v.getSoLuongTon());
        }
        if (d.getIdKhachHang() != null) x.setIdKhachHang(d.getIdKhachHang().getId());
        x.setTenKhachHang(d.getTenKhachHang());
        x.setSoDienThoai(d.getSoDienThoai());
        x.setEmail(d.getEmail());
        x.setSoLuong(d.getSoLuong());
        x.setNgayDangKy(ds(d.getNgayDangKy()));
        x.setNgayDuKien(ds(d.getNgayDuKien()));
        x.setTrangThai(d.getTrangThai());
        x.setGhiChu(d.getGhiChu());
        if (d.getIdHoaDon() != null) {
            x.setIdHoaDon(d.getIdHoaDon().getId());
            x.setMaHoaDon(d.getIdHoaDon().getMaHoaDon());
        }
        return x;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DatTruocDto> findAll() {
        return repo.findActive().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DatTruocDto findById(int id) {
        return repo.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public long demChoHang(int idSanPhamChiTiet) {
        return repo.countChoHang(idSanPhamChiTiet);
    }

    @Override
    @Transactional
    public DatTruocDto dangKy(DatTruocRequest req) {
        if (req.getIdSanPhamChiTiet() == null) throw new IllegalArgumentException("Thiếu sản phẩm cần đặt trước.");
        if (!StringUtils.hasText(req.getTenKhachHang())) throw new IllegalArgumentException("Nhập tên khách hàng.");
        if (!StringUtils.hasText(req.getSoDienThoai())) throw new IllegalArgumentException("Nhập số điện thoại.");
        SanPhamChiTiet v = sanPhamChiTietRepository.findById(req.getIdSanPhamChiTiet())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại."));
        int ton = v.getSoLuongTon() == null ? 0 : v.getSoLuongTon();
        if (ton > 0) {
            throw new IllegalStateException("Sản phẩm vẫn còn hàng — khách có thể mua trực tiếp, không cần đặt trước.");
        }
        DatTruoc d = new DatTruoc();
        d.setIdSanPhamChiTiet(v);
        if (req.getIdKhachHang() != null) {
            khachHangRepository.findById(req.getIdKhachHang()).ifPresent(d::setIdKhachHang);
        }
        d.setTenKhachHang(req.getTenKhachHang());
        d.setSoDienThoai(req.getSoDienThoai());
        d.setEmail(req.getEmail());
        d.setSoLuong(req.getSoLuong() == null || req.getSoLuong() < 1 ? 1 : req.getSoLuong());
        Instant now = Instant.now();
        d.setNgayDangKy(now);
        d.setNgayDuKien(parseDate(req.getNgayDuKien(), now.plusSeconds(60L * 60 * 24 * 30)));
        d.setTrangThai(CHO_HANG);
        d.setGhiChu(req.getGhiChu());
        d.setNgayTao(now);
        d.setNgayCapNhat(now);
        d.setTrangThaiXoa(false);
        d = repo.save(d);
        d.setMaDatTruoc("DT" + String.format("%04d", d.getId()));
        return toDto(repo.save(d));
    }

    private Instant parseDate(String s, Instant fallback) {
        if (!StringUtils.hasText(s)) return fallback;
        try {
            return LocalDate.parse(s.substring(0, 10)).atStartOfDay().toInstant(ZoneOffset.UTC);
        } catch (Exception e) {
            return fallback;
        }
    }

    @Override
    @Transactional
    public DatTruocDto capNhatTrangThai(int id, String trangThai) {
        DatTruoc d = require(id);
        d.setTrangThai(trangThai);
        d.setNgayCapNhat(Instant.now());
        return toDto(repo.save(d));
    }

    @Override
    @Transactional
    public DatTruocDto chuyenDon(int id, Integer idNhanVien) {
        DatTruoc d = require(id);
        if (DA_CHUYEN_DON.equals(d.getTrangThai())) {
            throw new IllegalStateException("Phiếu này đã được chuyển thành đơn hàng.");
        }
        if (DA_HUY.equals(d.getTrangThai())) {
            throw new IllegalStateException("Phiếu đã hủy, không thể chuyển đơn.");
        }
        SanPhamChiTiet v = d.getIdSanPhamChiTiet();
        if (v == null) throw new IllegalStateException("Phiếu không gắn sản phẩm.");
        int can = d.getSoLuong() == null ? 1 : d.getSoLuong();
        int ton = v.getSoLuongTon() == null ? 0 : v.getSoLuongTon();
        if (ton < can) {
            throw new IllegalStateException("Chưa đủ hàng để chuyển đơn (cần " + can + ", còn " + ton + ").");
        }
        // Tạo hóa đơn chờ và thêm hàng — tái dùng logic POS (đã tự trừ kho, giữ hàng cho khách)
        HoaDonDto inv = hoaDonService.createEmpty(idNhanVien);
        AddItemRequest item = new AddItemRequest();
        item.setIdSanPhamChiTiet(v.getId());
        item.setSoLuong(can);
        hoaDonService.addItem(inv.getId(), item);

        hoaDonRepository.findById(inv.getId()).ifPresent(h -> {
            if (d.getIdKhachHang() != null) h.setIdKhachHang(d.getIdKhachHang());
            h.setTenNguoiNhan(d.getTenKhachHang());
            h.setSoDienThoai(d.getSoDienThoai());
            h.setGhiChu("Từ phiếu đặt trước " + d.getMaDatTruoc());
            hoaDonRepository.save(h);
            d.setIdHoaDon(h);
        });
        d.setTrangThai(DA_CHUYEN_DON);
        d.setNgayCapNhat(Instant.now());
        return toDto(repo.save(d));
    }

    @Override
    @Transactional
    public void softDelete(int id) {
        DatTruoc d = require(id);
        d.setTrangThaiXoa(true);
        d.setNgayCapNhat(Instant.now());
        repo.save(d);
    }

    private DatTruoc require(int id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Phiếu đặt trước không tồn tại."));
    }
}
