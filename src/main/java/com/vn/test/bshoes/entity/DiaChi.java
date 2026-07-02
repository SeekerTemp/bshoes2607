package com.vn.test.bshoes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dia_chi", schema = "dbo")
public class DiaChi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dia_chi", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private com.vn.test.bshoes.entity.KhachHang idKhachHang;

    @Nationalized
    @Column(name = "dia_chi_mac_dinh")
    private String diaChiMacDinh;

    @Nationalized
    @Column(name = "thanh_pho", length = 50)
    private String thanhPho;

    @Nationalized
    @Column(name = "phuong", length = 50)
    private String phuong;

    @Nationalized
    @Column(name = "dia_chi_them")
    private String diaChiThem;

    @Nationalized
    @Column(name = "nguoi_tao", length = 50)
    private String nguoiTao;

    @Nationalized
    @Column(name = "nguoi_cap_nhat", length = 50)
    private String nguoiCapNhat;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_tao")
    private Instant ngayTao;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_cap_nhat")
    private Instant ngayCapNhat;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @ColumnDefault("0")
    @Column(name = "trang_thai_xoa")
    private Boolean trangThaiXoa;

}