package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.AddItemRequest;
import com.vn.test.bshoes.dto.CheckoutRequest;
import com.vn.test.bshoes.dto.HoaDonChiTietDto;
import com.vn.test.bshoes.dto.HoaDonDto;
import com.vn.test.bshoes.dto.PhieuGiamGiaDto;
import com.vn.test.bshoes.dto.PosSanPhamDto;
import com.vn.test.bshoes.dto.ThanhToanRequest;
import com.vn.test.bshoes.entity.HoaDon;
import com.vn.test.bshoes.entity.HoaDonChiTiet;
import com.vn.test.bshoes.entity.KhachHang;
import com.vn.test.bshoes.entity.LichSuHoaDon;
import com.vn.test.bshoes.entity.NhanVien;
import com.vn.test.bshoes.entity.PhieuGiamGia;
import com.vn.test.bshoes.entity.SanPhamChiTiet;
import com.vn.test.bshoes.repository.HoaDonChiTietRepository;
import com.vn.test.bshoes.repository.HoaDonRepository;
import com.vn.test.bshoes.repository.KhachHangRepository;
import com.vn.test.bshoes.repository.LichSuHoaDonRepository;
import com.vn.test.bshoes.repository.NhanVienRepository;
import com.vn.test.bshoes.repository.PhieuGiamGiaRepository;
import com.vn.test.bshoes.repository.SanPhamChiTietRepository;
import com.vn.test.bshoes.service.HoaDonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class HoaDonServiceImpl implements HoaDonService {

    /** Max number of pending (trang_thai=0) invoices open at once — parity with legacy POS. */
    private static final int MAX_PENDING = 3;

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final NhanVienRepository nhanVienRepository;
    private final KhachHangRepository khachHangRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final LichSuHoaDonRepository lichSuHoaDonRepository;

    public HoaDonServiceImpl(HoaDonRepository hoaDonRepository,
                             HoaDonChiTietRepository hoaDonChiTietRepository,
                             SanPhamChiTietRepository sanPhamChiTietRepository,
                             NhanVienRepository nhanVienRepository,
                             KhachHangRepository khachHangRepository,
                             PhieuGiamGiaRepository phieuGiamGiaRepository,
                             LichSuHoaDonRepository lichSuHoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.hoaDonChiTietRepository = hoaDonChiTietRepository;
        this.sanPhamChiTietRepository = sanPhamChiTietRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.khachHangRepository = khachHangRepository;
        this.phieuGiamGiaRepository = phieuGiamGiaRepository;
        this.lichSuHoaDonRepository = lichSuHoaDonRepository;
    }

    // ------------------------------------------------------------------ mapping

    private String trangThaiLabel(Integer trangThai) {
        if (trangThai == null) return "Khác";
        return switch (trangThai) {
            case 0 -> "Chờ";
            case 1 -> "Thành công";
            case 2 -> "Huỷ";
            case 3 -> "Chờ giao";
            case 4 -> "Đã giao";
            case 5 -> "Trả hàng";
            default -> "Khác";
        };
    }

    private HoaDonChiTietDto chiTietToDto(HoaDonChiTiet h) {
        SanPhamChiTiet spct = h.getIdSanPhamChiTiet();
        HoaDonChiTietDto dto = new HoaDonChiTietDto();
        dto.setId(h.getId());
        dto.setIdSanPhamChiTiet(spct != null ? spct.getId() : null);
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
        dto.setTrangThaiCode(h.getTrangThai());
        dto.setSoDienThoai(h.getSoDienThoai());
        dto.setDiaChi(h.getDiaChi());
        dto.setPhuongThucThanhToan(h.getPhuongThucThanhToan());
        dto.setGhiChu(h.getGhiChu());
        dto.setTongTienBanDau(h.getTongTienBanDau());
        dto.setTienGiamGia(h.getTienGiamGia());
        dto.setPhiShip(h.getPhiShip());
        List<HoaDonChiTietDto> chiTiet = activeLines(h.getId()).stream()
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

    // ------------------------------------------------------------------ reads

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

    // ------------------------------------------------------------------ cart / write

    @Override
    @Transactional
    public HoaDonDto create(HoaDonDto dto) {
        return createEmpty(null);
    }

    @Override
    @Transactional
    public HoaDonDto createEmpty(Integer idNhanVien) {
        if (hoaDonRepository.countByTrangThai(0) >= MAX_PENDING) {
            throw new IllegalStateException("Đã đạt tối đa " + MAX_PENDING + " hoá đơn chờ. Hãy thanh toán hoặc huỷ bớt.");
        }
        HoaDon h = new HoaDon();
        h.setTrangThai(0);
        h.setLoaiHoaDon(false);
        h.setTongTienBanDau(BigDecimal.ZERO);
        h.setTienGiamGia(BigDecimal.ZERO);
        h.setTongTienPhaiTra(BigDecimal.ZERO);
        Instant now = Instant.now();
        h.setNgayTaoMa(now);
        h.setNgayCapNhat(now);
        if (idNhanVien != null) {
            nhanVienRepository.findById(idNhanVien).ifPresent(h::setIdNhanVien);
            h.setNguoiTaoMa(h.getIdNhanVien() != null ? h.getIdNhanVien().getTenNhanVien() : null);
        }
        h = hoaDonRepository.save(h);
        h.setMaHoaDon("HD" + h.getId());
        h = hoaDonRepository.save(h);
        return toDto(h);
    }

    @Override
    @Transactional
    public HoaDonDto addItem(int idHoaDon, AddItemRequest req) {
        HoaDon h = requirePending(idHoaDon);
        int qty = (req.getSoLuong() == null || req.getSoLuong() < 1) ? 1 : req.getSoLuong();
        Integer spctId = req.getIdSanPhamChiTiet();
        if (spctId == null) throw new IllegalArgumentException("Thiếu id sản phẩm chi tiết.");
        SanPhamChiTiet spct = sanPhamChiTietRepository.findById(spctId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm chi tiết không tồn tại."));

        // Decrement stock atomically first — abort if not enough on hand.
        if (sanPhamChiTietRepository.decrementStock(spctId, qty) == 0) {
            throw new IllegalStateException("Không đủ tồn kho cho " + spct.getMaSanPhamChiTiet());
        }

        HoaDonChiTiet line = hoaDonChiTietRepository.findByHoaDonAndVariant(idHoaDon, spctId);
        Instant now = Instant.now();
        if (line != null) {
            line.setSoLuong(line.getSoLuong() + qty);
            line.setThanhTien(lineThanhTien(spct.getDonGia(), line.getSoLuong()));
            line.setNgayCapNhat(now);
        } else {
            line = new HoaDonChiTiet();
            line.setIdHoaDon(h);
            line.setIdSanPhamChiTiet(spct);
            line.setSoLuong(qty);
            line.setGiaGiam(BigDecimal.ZERO);
            line.setThanhTien(lineThanhTien(spct.getDonGia(), qty));
            line.setTrangThai(true);
            line.setTrangThaiXoa(false);
            line.setNgayTaoMa(now);
            line.setNgayCapNhat(now);
            line.setNguoiTao(req.getNguoiTao());
        }
        hoaDonChiTietRepository.save(line);
        recomputeTotals(h);
        return toDto(h);
    }

    @Override
    @Transactional
    public HoaDonDto updateItemQuantity(int idChiTiet, int soLuong) {
        HoaDonChiTiet line = hoaDonChiTietRepository.findById(idChiTiet)
                .orElseThrow(() -> new IllegalArgumentException("Dòng hoá đơn không tồn tại."));
        HoaDon h = requirePending(line.getIdHoaDon().getId());
        SanPhamChiTiet spct = line.getIdSanPhamChiTiet();
        int oldQty = line.getSoLuong() == null ? 0 : line.getSoLuong();

        if (soLuong <= 0) {
            // remove: return everything to stock and delete
            sanPhamChiTietRepository.incrementStock(spct.getId(), oldQty);
            hoaDonChiTietRepository.delete(line);
            recomputeTotals(h);
            return toDto(h);
        }

        int delta = soLuong - oldQty;
        if (delta > 0) {
            if (sanPhamChiTietRepository.decrementStock(spct.getId(), delta) == 0) {
                throw new IllegalStateException("Không đủ tồn kho cho " + spct.getMaSanPhamChiTiet());
            }
        } else if (delta < 0) {
            sanPhamChiTietRepository.incrementStock(spct.getId(), -delta);
        }
        line.setSoLuong(soLuong);
        line.setThanhTien(lineThanhTien(spct.getDonGia(), soLuong));
        line.setNgayCapNhat(Instant.now());
        hoaDonChiTietRepository.save(line);
        recomputeTotals(h);
        return toDto(h);
    }

    @Override
    @Transactional
    public HoaDonDto removeItem(int idChiTiet) {
        HoaDonChiTiet line = hoaDonChiTietRepository.findById(idChiTiet)
                .orElseThrow(() -> new IllegalArgumentException("Dòng hoá đơn không tồn tại."));
        HoaDon h = requirePending(line.getIdHoaDon().getId());
        int qty = line.getSoLuong() == null ? 0 : line.getSoLuong();
        sanPhamChiTietRepository.incrementStock(line.getIdSanPhamChiTiet().getId(), qty);
        hoaDonChiTietRepository.delete(line);
        recomputeTotals(h);
        return toDto(h);
    }

    @Override
    @Transactional
    public HoaDonDto thanhToan(int idHoaDon, ThanhToanRequest req) {
        HoaDon h = requirePending(idHoaDon);
        List<HoaDonChiTiet> lines = activeLines(idHoaDon);
        if (lines.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng trống, không thể thanh toán.");
        }

        // customer / staff / recipient
        if (req.getIdNhanVien() != null) {
            nhanVienRepository.findById(req.getIdNhanVien()).ifPresent(h::setIdNhanVien);
        }
        if (req.getIdKhachHang() != null) {
            khachHangRepository.findById(req.getIdKhachHang()).ifPresent(h::setIdKhachHang);
        }
        if (req.getTenNguoiNhan() != null) h.setTenNguoiNhan(req.getTenNguoiNhan());
        if (req.getSoDienThoai() != null) h.setSoDienThoai(req.getSoDienThoai());
        if (req.getDiaChi() != null) h.setDiaChi(req.getDiaChi());
        if (req.getGhiChu() != null) h.setGhiChu(req.getGhiChu());
        h.setPhuongThucThanhToan(req.getPhuongThucThanhToan() != null ? req.getPhuongThucThanhToan() : "Tiền mặt");

        // totals + voucher
        BigDecimal banDau = sumLines(lines);
        BigDecimal giam = BigDecimal.ZERO;
        if (req.getIdPhieuGiamGia() != null) {
            PhieuGiamGia pgg = phieuGiamGiaRepository.findById(req.getIdPhieuGiamGia()).orElse(null);
            if (pgg != null) {
                h.setIdPhieuGiamGia(pgg);
                BigDecimal d = hoaDonRepository.tinhGiamGia(pgg.getId(), banDau);
                if (d != null) giam = d;
            }
        }
        BigDecimal phiShip = req.getPhiShip() != null ? req.getPhiShip() : BigDecimal.ZERO;
        h.setPhiShip(phiShip);
        h.setTongTienBanDau(banDau);
        h.setTienGiamGia(giam);
        h.setTongTienPhaiTra(banDau.subtract(giam).add(phiShip).max(BigDecimal.ZERO));

        // finalize — delivery orders go to "Chờ giao" (3), counter sales to "Thành công" (1)
        boolean giao = Boolean.TRUE.equals(req.getGiaoHang());
        h.setTrangThai(giao ? 3 : 1);
        h.setLoaiHoaDon(true);
        h.setNgayCapNhat(Instant.now());
        String staff = h.getIdNhanVien() != null ? h.getIdNhanVien().getTenNhanVien() : "admin";
        h.setNguoiCapNhat(staff);
        hoaDonRepository.save(h);

        writeHistory(h, (giao ? "Đặt hàng giao - " : "Thanh toán hóa đơn: ") + h.getMaHoaDon()
                + " | Tổng tiền: " + h.getTongTienPhaiTra(), true);
        return toDto(h);
    }

    @Override
    @Transactional
    public HoaDonDto daGiao(int idHoaDon) {
        HoaDon h = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new IllegalArgumentException("Hoá đơn không tồn tại."));
        if (h.getTrangThai() == null || h.getTrangThai() != 3) {
            throw new IllegalStateException("Chỉ đơn 'Chờ giao' mới có thể đánh dấu 'Đã giao'.");
        }
        h.setTrangThai(4);
        h.setNgayCapNhat(Instant.now());
        hoaDonRepository.save(h);
        writeHistory(h, "Đã giao hàng - " + h.getMaHoaDon(), true);
        return toDto(h);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HoaDonDto> findBySoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.isBlank()) return List.of();
        return hoaDonRepository.findBySoDienThoai(soDienThoai.trim()).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public HoaDonDto datHangOnline(CheckoutRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng trống.");
        }
        if (req.getTenNguoiNhan() == null || req.getTenNguoiNhan().isBlank()) {
            throw new IllegalArgumentException("Nhập tên người nhận.");
        }
        if (req.getSoDienThoai() == null || req.getSoDienThoai().isBlank()) {
            throw new IllegalArgumentException("Nhập số điện thoại.");
        }
        if (req.getDiaChi() == null || req.getDiaChi().isBlank()) {
            throw new IllegalArgumentException("Nhập địa chỉ nhận hàng.");
        }

        Instant now = Instant.now();
        HoaDon h = new HoaDon();
        h.setTrangThai(3);            // Chờ giao — nhân viên xử lý tiếp ở màn Giao Hàng
        h.setLoaiHoaDon(false);       // COD: chưa thu tiền
        h.setPhuongThucThanhToan("COD");
        if (req.getIdKhachHang() != null) {
            khachHangRepository.findById(req.getIdKhachHang()).ifPresent(h::setIdKhachHang);
        }
        h.setTenNguoiNhan(req.getTenNguoiNhan().trim());
        h.setSoDienThoai(req.getSoDienThoai().trim());
        h.setDiaChi(req.getDiaChi().trim());
        h.setGhiChu(req.getGhiChu());
        h.setTongTienBanDau(BigDecimal.ZERO);
        h.setTienGiamGia(BigDecimal.ZERO);
        h.setTongTienPhaiTra(BigDecimal.ZERO);
        h.setNgayTaoMa(now);
        h.setNgayCapNhat(now);
        h.setNguoiTaoMa(h.getTenNguoiNhan());
        h = hoaDonRepository.save(h);
        h.setMaHoaDon("HD" + h.getId());
        h = hoaDonRepository.save(h);

        for (AddItemRequest item : req.getItems()) {
            Integer spctId = item.getIdSanPhamChiTiet();
            if (spctId == null) throw new IllegalArgumentException("Thiếu id sản phẩm chi tiết.");
            int qty = (item.getSoLuong() == null || item.getSoLuong() < 1) ? 1 : item.getSoLuong();
            SanPhamChiTiet spct = sanPhamChiTietRepository.findById(spctId)
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm chi tiết không tồn tại."));
            // Cùng cách trừ kho atomic như POS: hết hàng thì rollback cả đơn.
            if (sanPhamChiTietRepository.decrementStock(spctId, qty) == 0) {
                throw new IllegalStateException("Không đủ tồn kho cho " + spct.getMaSanPhamChiTiet()
                        + " — vui lòng giảm số lượng hoặc đặt trước.");
            }
            HoaDonChiTiet line = new HoaDonChiTiet();
            line.setIdHoaDon(h);
            line.setIdSanPhamChiTiet(spct);
            line.setSoLuong(qty);
            line.setThanhTien(lineThanhTien(spct.getDonGia(), qty));
            line.setNgayTaoMa(now);
            line.setNgayCapNhat(now);
            line.setTrangThai(true);
            line.setTrangThaiXoa(false);
            hoaDonChiTietRepository.save(line);
        }
        recomputeTotals(h);
        writeHistory(h, "Khách đặt hàng online - " + h.getMaHoaDon(), true);
        return toDto(h);
    }

    @Override
    @Transactional
    public HoaDonDto traHang(int idHoaDon, Integer idNhanVien) {
        HoaDon h = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new IllegalArgumentException("Hoá đơn không tồn tại."));
        Integer tt = h.getTrangThai();
        if (tt == null || !(tt == 1 || tt == 3 || tt == 4)) {
            throw new IllegalStateException("Chỉ đơn đã bán/đã giao mới có thể trả hàng.");
        }
        for (HoaDonChiTiet line : activeLines(idHoaDon)) {
            int qty = line.getSoLuong() == null ? 0 : line.getSoLuong();
            sanPhamChiTietRepository.incrementStock(line.getIdSanPhamChiTiet().getId(), qty);
        }
        if (idNhanVien != null) nhanVienRepository.findById(idNhanVien).ifPresent(h::setIdNhanVien);
        h.setTrangThai(5);
        h.setNgayCapNhat(Instant.now());
        hoaDonRepository.save(h);
        writeHistory(h, "Trả hàng - " + h.getMaHoaDon(), false);
        return toDto(h);
    }

    @Override
    @Transactional
    public HoaDonDto huy(int idHoaDon, Integer idNhanVien) {
        HoaDon h = requirePending(idHoaDon);
        // return every line's units to stock, then drop the lines
        for (HoaDonChiTiet line : activeLines(idHoaDon)) {
            int qty = line.getSoLuong() == null ? 0 : line.getSoLuong();
            sanPhamChiTietRepository.incrementStock(line.getIdSanPhamChiTiet().getId(), qty);
            hoaDonChiTietRepository.delete(line);
        }
        if (idNhanVien != null) {
            nhanVienRepository.findById(idNhanVien).ifPresent(h::setIdNhanVien);
        }
        h.setTrangThai(2);
        h.setNgayCapNhat(Instant.now());
        hoaDonRepository.save(h);
        writeHistory(h, "Huỷ hóa đơn: " + h.getMaHoaDon(), false);
        return toDto(h);
    }

    // ------------------------------------------------------------------ helpers

    private HoaDon requirePending(int idHoaDon) {
        HoaDon h = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new IllegalArgumentException("Hoá đơn không tồn tại."));
        if (h.getTrangThai() != null && h.getTrangThai() != 0) {
            throw new IllegalStateException("Hoá đơn đã được xử lý (không còn ở trạng thái chờ).");
        }
        return h;
    }

    private List<HoaDonChiTiet> activeLines(int idHoaDon) {
        return hoaDonChiTietRepository.findByHoaDon(idHoaDon).stream()
                .filter(l -> l.getTrangThaiXoa() == null || !l.getTrangThaiXoa())
                .toList();
    }

    private BigDecimal lineThanhTien(BigDecimal donGia, int soLuong) {
        if (donGia == null) return BigDecimal.ZERO;
        return donGia.multiply(BigDecimal.valueOf(soLuong));
    }

    private BigDecimal sumLines(List<HoaDonChiTiet> lines) {
        return lines.stream()
                .map(l -> l.getThanhTien() != null ? l.getThanhTien() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Recompute pre-voucher and payable totals from the current lines (voucher applied at payment). */
    private void recomputeTotals(HoaDon h) {
        BigDecimal banDau = sumLines(activeLines(h.getId()));
        BigDecimal giam = h.getTienGiamGia() != null ? h.getTienGiamGia() : BigDecimal.ZERO;
        h.setTongTienBanDau(banDau);
        h.setTongTienPhaiTra(banDau.subtract(giam).max(BigDecimal.ZERO));
        h.setNgayCapNhat(Instant.now());
        hoaDonRepository.save(h);
    }

    private void writeHistory(HoaDon h, String ghiChu, boolean trangThai) {
        LichSuHoaDon ls = new LichSuHoaDon();
        ls.setIdHoaDon(h);
        NhanVien nv = h.getIdNhanVien();
        ls.setIdNhanVien(nv);
        ls.setGhiChu(ghiChu);
        Instant now = Instant.now();
        ls.setThoiGianThayDoi(now);
        ls.setNgayTaoMa(now);
        ls.setNgayCapNhat(now);
        ls.setNguoiTaoMa(nv != null ? nv.getTenNhanVien() : "admin");
        ls.setNguoiCapNhat(nv != null ? nv.getTenNhanVien() : "admin");
        ls.setTrangThai(trangThai);
        ls.setTrangThaiXoa(false);
        lichSuHoaDonRepository.save(ls);
    }
}
