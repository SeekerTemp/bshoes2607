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
@Table(name = "phieu_giam_gia", schema = "dbo")
public class PhieuGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_phieu_giam_gia", nullable = false)
    private Integer id;

    @Column(name = "ma_phieu_giam", length = 20)
    private String maPhieuGiam;

    @Nationalized
    @Column(name = "ten_phieu_giam", length = 100)
    private String tenPhieuGiam;

    @Column(name = "loai_giam_gia")
    private Integer loaiGiamGia;

    @Column(name = "gia_tri_giam")
    private BigDecimal giaTriGiam;

    @Column(name = "don_toi_thieu")
    private BigDecimal donToiThieu;

    @Column(name = "giam_toi_da")
    private BigDecimal giamToiDa;

    @Column(name = "so_luong")
    private Integer soLuong;

    @ColumnDefault("getdate()")
    @Column(name = "thoi_gian_bat_dau")
    private Instant thoiGianBatDau;

    @ColumnDefault("getdate()")
    @Column(name = "thoi_gian_ket_thuc")
    private Instant thoiGianKetThuc;

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

    @OneToMany(mappedBy = "idPhieuGiamGia")
    private Set<HoaDon> hoaDons = new LinkedHashSet<>();

}