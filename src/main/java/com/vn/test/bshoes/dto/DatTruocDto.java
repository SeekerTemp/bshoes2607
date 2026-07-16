package com.vn.test.bshoes.dto;

import lombok.Data;

import java.math.BigDecimal;

/** Phiếu đặt trước — shape cho cả màn quản lý và trang cửa hàng. */
@Data
public class DatTruocDto {
    private Integer id;
    private String ma;
    // sản phẩm
    private Integer idSanPhamChiTiet;
    private String tenSanPham;
    private String mauSize;
    private String imageUrl;
    private BigDecimal gia;
    private Integer ton;
    // khách
    private Integer idKhachHang;
    private String tenKhachHang;
    private String soDienThoai;
    private String email;
    // phiếu
    private Integer soLuong;
    private String ngayDangKy;
    private String ngayDuKien;
    private String trangThai;
    private String ghiChu;
    private Integer idHoaDon;
    private String maHoaDon;
}
