package com.vn.test.bshoes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "nhan_vien", schema = "dbo")
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nhan_vien", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vai_tro")
    private com.vn.test.bshoes.entity.VaiTro idVaiTro;

    @Column(name = "ma_nhan_vien", length = 20)
    private String maNhanVien;

    @Nationalized
    @Column(name = "ten_nhan_vien", length = 100)
    private String tenNhanVien;

    @Column(name = "tai_khoan", length = 50)
    private String taiKhoan;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "mat_khau", length = 50)
    private String matKhau;

    @Column(name = "cccd", length = 20)
    private String cccd;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Nationalized
    @Column(name = "dia_chi")
    private String diaChi;

    @Nationalized
    @Column(name = "chuc_vu", length = 50)
    private String chucVu;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_sinh")
    private Instant ngaySinh;

    @Nationalized
    @Column(name = "gioi_tinh", length = 10)
    private String gioiTinh;

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
    private Boolean trangThai;

    @ColumnDefault("0")
    @Column(name = "trang_thai_xoa")
    private Boolean trangThaiXoa;

    @OneToMany(mappedBy = "idNhanVien")
    private Set<HoaDon> hoaDons = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idNhanVien")
    private Set<LichSuHoaDon> lichSuHoaDons = new LinkedHashSet<>();

}