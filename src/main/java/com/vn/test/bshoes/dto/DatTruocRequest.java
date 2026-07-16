package com.vn.test.bshoes.dto;

import lombok.Data;

/** Request khách đăng ký đặt trước từ trang cửa hàng. */
@Data
public class DatTruocRequest {
    private Integer idSanPhamChiTiet;
    private Integer idKhachHang;      // tùy chọn — khách vãng lai để trống
    private String tenKhachHang;
    private String soDienThoai;
    private String email;
    private Integer soLuong;          // mặc định 1
    private String ngayDuKien;        // ISO date, tùy chọn
    private String ghiChu;
}
