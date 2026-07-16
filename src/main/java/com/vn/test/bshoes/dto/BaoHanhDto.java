package com.vn.test.bshoes.dto;

import lombok.Data;

import java.math.BigDecimal;

/** Frontend-shaped warranty record (matches the bảo hành screen). */
@Data
public class BaoHanhDto {
    private Integer id;
    private String ma;
    // product
    private Integer idSanPhamChiTiet;
    private String model;       // shoe name
    private String mauSize;     // "Đen / 42"
    private String serial;
    private String imageUrl;
    // customer / invoice
    private Integer idKhachHang;
    private String maKH;
    private String tenKH;
    private String sdt;
    private Integer idHoaDon;
    private String maHD;
    // handling
    private Integer idNhanVien;
    private String nv;
    private String donVi;
    private String loai;
    private String moTa;
    private BigDecimal chiPhi;
    private Boolean thayLinhKien;
    private String batDau;
    private String hetHan;
    private String trangThai;
    private String cat;         // derived tab bucket: wait/pickup/expired/success/fail
}
