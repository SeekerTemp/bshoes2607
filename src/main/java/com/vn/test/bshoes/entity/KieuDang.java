package com.vn.test.bshoes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "kieu_dang", schema = "dbo")
public class KieuDang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kieu_dang", nullable = false)
    private Integer id;

    @Column(name = "ma_kieu_dang", length = 20)
    private String maKieuDang;

    @Nationalized
    @Column(name = "ten_kieu_dang", length = 100)
    private String tenKieuDang;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @ColumnDefault("0")
    @Column(name = "trang_thai_xoa")
    private Boolean trangThaiXoa;

    @OneToMany(mappedBy = "idKieuDang")
    private Set<com.vn.test.bshoes.entity.SanPham> sanPhams = new LinkedHashSet<>();

}