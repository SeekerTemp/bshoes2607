package com.vn.test.bshoes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

/** Phiếu đăng ký đặt trước (pre-order) một biến thể đang hết hàng. */
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dat_truoc", schema = "dbo")
public class DatTruoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dat_truoc", nullable = false)
    private Integer id;

    @Column(name = "ma_dat_truoc", length = 20)
    private String maDatTruoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham_chi_tiet")
    private SanPhamChiTiet idSanPhamChiTiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang idKhachHang;

    /** Điền khi phiếu đã được chuyển thành hóa đơn. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don")
    private HoaDon idHoaDon;

    @Nationalized
    @Column(name = "ten_khach_hang", length = 100)
    private String tenKhachHang;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "so_luong")
    private Integer soLuong;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_dang_ky")
    private Instant ngayDangKy;

    @Column(name = "ngay_du_kien")
    private Instant ngayDuKien;

    @Nationalized
    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @Nationalized
    @Column(name = "ghi_chu")
    private String ghiChu;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_tao")
    private Instant ngayTao;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_cap_nhat")
    private Instant ngayCapNhat;

    @ColumnDefault("0")
    @Column(name = "trang_thai_xoa")
    private Boolean trangThaiXoa;
}
