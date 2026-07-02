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
@Table(name = "xuat_su", schema = "dbo")
public class XuatSu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_xuat_su", nullable = false)
    private Integer id;

    @Column(name = "ma_xuat_su", length = 20)
    private String maXuatSu;

    @Nationalized
    @Column(name = "ten_xuat_su", length = 100)
    private String tenXuatSu;

    @Nationalized
    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @ColumnDefault("0")
    @Column(name = "trang_thai_xoa")
    private Boolean trangThaiXoa;

    @OneToMany(mappedBy = "idXuatSu")
    private Set<SanPham> sanPhams = new LinkedHashSet<>();

}