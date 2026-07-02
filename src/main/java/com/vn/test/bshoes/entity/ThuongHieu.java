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
@Table(name = "thuong_hieu", schema = "dbo")
public class ThuongHieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_thuong_hieu", nullable = false)
    private Integer id;

    @Column(name = "ma_thuong_hieu", length = 20)
    private String maThuongHieu;

    @Nationalized
    @Column(name = "ten_thuong_hieu", length = 100)
    private String tenThuongHieu;

    @Nationalized
    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @ColumnDefault("0")
    @Column(name = "trang_thai_xoa")
    private Boolean trangThaiXoa;

    @OneToMany(mappedBy = "idThuongHieu")
    private Set<SanPham> sanPhams = new LinkedHashSet<>();

}