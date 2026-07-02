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
@Table(name = "kieu_day_giay", schema = "dbo")
public class KieuDayGiay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kieu_day_giay", nullable = false)
    private Integer id;

    @Column(name = "ma_day_giay", length = 20)
    private String maDayGiay;

    @Nationalized
    @Column(name = "ten_day_giay", length = 100)
    private String tenDayGiay;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @ColumnDefault("0")
    @Column(name = "trang_thai_xoa")
    private Boolean trangThaiXoa;

    @OneToMany(mappedBy = "idKieuDayGiay")
    private Set<com.vn.test.bshoes.entity.SanPham> sanPhams = new LinkedHashSet<>();

}