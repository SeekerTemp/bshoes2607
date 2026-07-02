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
@Table(name = "loai_san_pham", schema = "dbo")
public class LoaiSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_loai_san_pham", nullable = false)
    private Integer id;

    @Column(name = "ma_loai_san_pham", length = 20)
    private String maLoaiSanPham;

    @Nationalized
    @Column(name = "ten_loai_san_pham", length = 100)
    private String tenLoaiSanPham;

    @Nationalized
    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @ColumnDefault("0")
    @Column(name = "trang_thai_xoa")
    private Boolean trangThaiXoa;

    @OneToMany(mappedBy = "idLoaiSanPham")
    private Set<com.vn.test.bshoes.entity.SanPham> sanPhams = new LinkedHashSet<>();

}