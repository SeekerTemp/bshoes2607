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
@Table(name = "khach_hang", schema = "dbo")
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_khach_hang", nullable = false)
    private Integer id;

    @Column(name = "ma_khach_hang", length = 20)
    private String maKhachHang;

    @Nationalized
    @Column(name = "ten_khach_hang", length = 100)
    private String tenKhachHang;

    @Nationalized
    @Column(name = "gioi_tinh", length = 10)
    private String gioiTinh;

    // Cột DDL là varchar, không @Nationalized (xem ghi chú cùng chỗ ở HoaDon).
    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Nationalized
    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "email", length = 100)
    private String email;

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

    @OneToMany(mappedBy = "idKhachHang")
    private Set<DiaChi> diaChis = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idKhachHang")
    private Set<HoaDon> hoaDons = new LinkedHashSet<>();

}