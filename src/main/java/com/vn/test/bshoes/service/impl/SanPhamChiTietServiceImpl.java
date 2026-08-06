package com.vn.test.bshoes.service.impl;

import com.vn.test.bshoes.dto.BienTheDto;
import com.vn.test.bshoes.dto.PosSanPhamDto;
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
        v.setTrangThai(true);
        v.setTrangThaiXoa(false);
        // Lưu lần đầu để lấy id, rồi mới sinh mã — mã luôn do server đặt, không nhận
        // từ client (xem sinhMa).
        v = repo.save(v);
        v.setMaSanPhamChiTiet(sinhMa(v));
        return toDto(repo.save(v));
    }

    /**
     * Mã biến thể: {@code SPCT<id 3 chữ số>-<mã màu>-<mã kích cỡ>}, ví dụ SPCT079-BK-M.
     *
     * Phần id đảm bảo DUY NHẤT, phần màu/kích cỡ để người dùng đọc được ngay trên nhãn.
     * Trước đây mã là {@code "SPCT" + mã sản phẩm cha}, nên MỌI biến thể của cùng một
     * sản phẩm mang đúng một mã: quét QR theo mã (findByMaSanPhamChiTiet) trả về một
     * biến thể bất kỳ trong số đó.
     *
     * Là hàm thuần theo (id, màu, kích cỡ): id không đổi nên mã chỉ đổi khi màu hoặc
     * kích cỡ đổi — đúng lúc mã cũ đã sai. Nhãn/QR đã in của biến thể đó cần in lại.
     */
    private String sinhMa(SanPhamChiTiet v) {
        String mau = v.getIdMauSac() != null && StringUtils.hasText(v.getIdMauSac().getMaMauSac())
                ? v.getIdMauSac().getMaMauSac() : "NA";
        String size = v.getIdKichCo() != null && StringUtils.hasText(v.getIdKichCo().getMaKichCo())
                ? v.getIdKichCo().getMaKichCo() : "NA";
        return String.format("SPCT%03d-%s-%s", v.getId(), mau, size);
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
        if (StringUtils.hasText(dto.getImageUrl())) v.setImageUrl(dto.getImageUrl());
        // Mã do server sở hữu, dto.getMa() bị bỏ qua có chủ ý: sinh lại sau khi màu /
        // kích cỡ đã cập nhật để mã không bao giờ mô tả sai biến thể.
        v.setMaSanPhamChiTiet(sinhMa(v));
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
        if (repo.softDeleteById(id) == 0) {
            throw new IllegalArgumentException("Không tìm thấy biến thể để ẩn");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BienTheDto> findRecycle() {
        return repo.findRecycle().stream().map(v -> {
            BienTheDto dto = toDto(v);
            dto.setTenSanPham(v.getIdSanPham() != null ? v.getIdSanPham().getTenSanPham() : null);
            return dto;
        }).toList();
    }

    @Override
    @Transactional
    public void restore(int id) {
        if (repo.restoreById(id) == 0) {
            throw new IllegalArgumentException("Không tìm thấy biến thể để khôi phục");
        }
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
