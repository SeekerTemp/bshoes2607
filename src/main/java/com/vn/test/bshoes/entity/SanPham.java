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
@Table(name = "san_pham", schema = "dbo")
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_san_pham", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai_san_pham")
    private LoaiSanPham idLoaiSanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chat_lieu")
    private ChatLieu idChatLieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kieu_dang")
    private KieuDang idKieuDang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kieu_co_giay")
    private KieuCoGiay idKieuCoGiay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kieu_day_giay")
    private KieuDayGiay idKieuDayGiay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thuong_hieu")
    private com.vn.test.bshoes.entity.ThuongHieu idThuongHieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_xuat_su")
    private com.vn.test.bshoes.entity.XuatSu idXuatSu;

    @Column(name = "ma_san_pham", length = 20)
    private String maSanPham;

    @Nationalized
    @Column(name = "ten_san_pham", length = 100)
    private String tenSanPham;

    @Nationalized
    @Column(name = "mo_ta")
    private String moTa;

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

    @OneToMany(mappedBy = "idSanPham")
    private Set<com.vn.test.bshoes.entity.SanPhamChiTiet> sanPhamChiTiets = new LinkedHashSet<>();

}