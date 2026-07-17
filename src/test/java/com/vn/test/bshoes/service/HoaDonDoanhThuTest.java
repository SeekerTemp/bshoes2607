package com.vn.test.bshoes.service;

import com.vn.test.bshoes.entity.HoaDon;
import com.vn.test.bshoes.entity.HoaDonChiTiet;
import com.vn.test.bshoes.entity.SanPhamChiTiet;
import com.vn.test.bshoes.repository.HoaDonChiTietRepository;
import com.vn.test.bshoes.repository.HoaDonRepository;
import com.vn.test.bshoes.repository.KhachHangRepository;
import com.vn.test.bshoes.repository.LichSuHoaDonRepository;
import com.vn.test.bshoes.repository.NhanVienRepository;
import com.vn.test.bshoes.repository.PhieuGiamGiaRepository;
import com.vn.test.bshoes.repository.SanPhamChiTietRepository;
import com.vn.test.bshoes.service.impl.HoaDonServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Khóa lại quan hệ giữa loai_hoa_don và doanh thu.
 *
 * <p>Mọi truy vấn doanh thu trong ThongKeRepository đều lọc {@code loai_hoa_don = 1} và
 * KHÔNG nhìn trang_thai. Cờ này vì thế là thứ quyết định một hóa đơn có được tính tiền
 * hay không, dễ quên khi đổi trạng thái. Hai test dưới đây chốt hai đầu:
 * giao hàng COD thì tiền vào, trả hàng thì tiền ra.
 */
class HoaDonDoanhThuTest {

    private HoaDonRepository hoaDonRepo;
    private HoaDonChiTietRepository chiTietRepo;
    private SanPhamChiTietRepository spctRepo;
    private LichSuHoaDonRepository lichSuRepo;
    private HoaDonServiceImpl service;

    @BeforeEach
    void setUp() {
        hoaDonRepo = mock(HoaDonRepository.class);
        chiTietRepo = mock(HoaDonChiTietRepository.class);
        spctRepo = mock(SanPhamChiTietRepository.class);
        lichSuRepo = mock(LichSuHoaDonRepository.class);
        service = new HoaDonServiceImpl(
                hoaDonRepo, chiTietRepo, spctRepo,
                mock(NhanVienRepository.class), mock(KhachHangRepository.class),
                mock(PhieuGiamGiaRepository.class), lichSuRepo);
        when(chiTietRepo.findByHoaDon(anyInt())).thenReturn(List.of());
        when(hoaDonRepo.save(any(HoaDon.class))).thenAnswer(i -> i.getArgument(0));
    }

    private HoaDon hoaDon(int id, int trangThai, boolean daThu) {
        HoaDon h = new HoaDon();
        h.setId(id);
        h.setMaHoaDon("HD" + id);
        h.setTrangThai(trangThai);
        h.setLoaiHoaDon(daThu);
        h.setTongTienPhaiTra(new BigDecimal("450000"));
        h.setTienGiamGia(BigDecimal.ZERO);
        return h;
    }

    @Test
    @DisplayName("Giao xong đơn COD thì đánh dấu đã thu tiền, nếu không doanh thu mất đơn online")
    void daGiaoThiVaoDoanhThu() {
        // đơn đặt online: Chờ giao, chưa thu tiền
        HoaDon h = hoaDon(10, 3, false);
        when(hoaDonRepo.findById(10)).thenReturn(Optional.of(h));

        service.daGiao(10);

        assertThat(h.getTrangThai()).isEqualTo(4);
        assertThat(h.getLoaiHoaDon())
                .as("ThongKe cong doanh thu theo loai_hoa_don = 1; de false thi don COD giao xong "
                        + "van khong duoc tinh tien")
                .isTrue();
    }

    @Test
    @DisplayName("Đơn quầy đã trả trước: giao xong vẫn giữ đã thu, không nhân đôi gì")
    void daGiaoDonTraTruocKhongDoi() {
        HoaDon h = hoaDon(11, 3, true);   // thanhToan tại quầy đã set true
        when(hoaDonRepo.findById(11)).thenReturn(Optional.of(h));

        service.daGiao(11);

        assertThat(h.getLoaiHoaDon()).isTrue();
    }

    @Test
    @DisplayName("Trả hàng thì rút khỏi doanh thu, nếu không đơn đã hoàn tiền vẫn bị tính là có thu")
    void traHangThiRutKhoiDoanhThu() {
        HoaDon h = hoaDon(12, 4, true);   // đã giao, đã thu
        when(hoaDonRepo.findById(12)).thenReturn(Optional.of(h));

        service.traHang(12, null);

        assertThat(h.getTrangThai()).isEqualTo(5);
        assertThat(h.getLoaiHoaDon())
                .as("doanh thu cong theo loai_hoa_don ma khong nhin trang_thai, nen tra hang "
                        + "phai ha co xuong false")
                .isFalse();
    }

    @Test
    @DisplayName("Trả hàng phải hoàn lại kho từng dòng hàng")
    void traHangHoanKho() {
        HoaDon h = hoaDon(13, 1, true);
        when(hoaDonRepo.findById(13)).thenReturn(Optional.of(h));
        SanPhamChiTiet v = new SanPhamChiTiet();
        v.setId(7);
        HoaDonChiTiet line = new HoaDonChiTiet();
        line.setIdHoaDon(h);
        line.setIdSanPhamChiTiet(v);
        line.setSoLuong(3);
        line.setTrangThaiXoa(false);
        when(chiTietRepo.findByHoaDon(13)).thenReturn(List.of(line));

        service.traHang(13, null);

        org.mockito.Mockito.verify(spctRepo).incrementStock(7, 3);
    }
}
