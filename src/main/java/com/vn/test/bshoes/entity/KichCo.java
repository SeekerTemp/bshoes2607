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
@Table(name = "kich_co", schema = "dbo")
public class KichCo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kich_co", nullable = false)
    private Integer id;

    @Column(name = "ma_kich_co", length = 20)
    private String maKichCo;

    @Nationalized
    @Column(name = "ten_kich_co", length = 50)
    private String tenKichCo;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @ColumnDefault("0")
    @Column(name = "trang_thai_xoa")
    private Boolean trangThaiXoa;

    @OneToMany(mappedBy = "idKichCo")
    private Set<com.vn.test.bshoes.entity.SanPhamChiTiet> sanPhamChiTiets = new LinkedHashSet<>();

}