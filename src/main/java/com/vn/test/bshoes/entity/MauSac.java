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
@Table(name = "mau_sac", schema = "dbo")
public class MauSac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mau_sac", nullable = false)
    private Integer id;

    @Column(name = "ma_mau_sac", length = 20)
    private String maMauSac;

    @Nationalized
    @Column(name = "ten_mau_sac", length = 50)
    private String tenMauSac;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @ColumnDefault("0")
    @Column(name = "trang_thai_xoa")
    private Boolean trangThaiXoa;

    @OneToMany(mappedBy = "idMauSac")
    private Set<com.vn.test.bshoes.entity.SanPhamChiTiet> sanPhamChiTiets = new LinkedHashSet<>();

}