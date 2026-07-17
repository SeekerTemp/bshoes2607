package com.vn.test.bshoes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hoa_don", schema = "dbo")
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hoa_don", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private com.vn.test.bshoes.entity.KhachHang idKhachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia")
    private com.vn.test.bshoes.entity.PhieuGiamGia idPhieuGiamGia;

    @Column(name = "ma_hoa_don", length = 20)
    private String maHoaDon;

    @Column(name = "tong_tien_ban_dau")
    private BigDecimal tongTienBanDau;

    @Column(name = "tien_giam_gia")
    private BigDecimal tienGiamGia;

    @Column(name = "tong_tien_phai_tra")
    private BigDecimal tongTienPhaiTra;

    @Column(name = "phi_ship")
    private BigDecimal phiShip;

    @Nationalized
    @Column(name = "ten_nguoi_nhan", length = 100)
    private String tenNguoiNhan;

    // Cột DDL là varchar (số điện thoại chỉ có chữ số) nên không @Nationalized: bind
    // NVARCHAR vào cột VARCHAR làm SQL Server ép kiểu ngầm cả cột, mất index seek ở
    // truy vấn tra đơn theo số điện thoại.
    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Nationalized
    @Column(name = "dia_chi")
    private String diaChi;

    @Nationalized
    @Column(name = "phuong_thuc_thanh_toan", length = 50)
    private String phuongThucThanhToan;

    @Nationalized
    @Column(name = "ghi_chu")
    private String ghiChu;

    @Nationalized
    @Column(name = "nguoi_tao_ma", length = 50)
    private String nguoiTaoMa;

    @Nationalized
    @Column(name = "nguoi_cap_nhat", length = 50)
    private String nguoiCapNhat;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_tao_ma")
    private Instant ngayTaoMa;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_cap_nhat")
    private Instant ngayCapNhat;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "loai_hoa_don")
    private Boolean loaiHoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private com.vn.test.bshoes.entity.NhanVien idNhanVien;

    @OneToMany(mappedBy = "idHoaDon")
    private Set<com.vn.test.bshoes.entity.HoaDonChiTiet> hoaDonChiTiets = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idHoaDon")
    private Set<com.vn.test.bshoes.entity.LichSuHoaDon> lichSuHoaDons = new LinkedHashSet<>();

}