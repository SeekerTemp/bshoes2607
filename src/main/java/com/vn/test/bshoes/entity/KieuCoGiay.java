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
@Table(name = "kieu_co_giay", schema = "dbo")
public class KieuCoGiay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kieu_co_giay", nullable = false)
    private Integer id;

    @Column(name = "ma_co_giay", length = 20)
    private String maCoGiay;

    @Nationalized
    @Column(name = "ten_co_giay", length = 100)
    private String tenCoGiay;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @ColumnDefault("0")
    @Column(name = "trang_thai_xoa")
    private Boolean trangThaiXoa;

    @OneToMany(mappedBy = "idKieuCoGiay")
    private Set<com.vn.test.bshoes.entity.SanPham> sanPhams = new LinkedHashSet<>();

}