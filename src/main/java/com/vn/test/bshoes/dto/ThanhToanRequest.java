package com.vn.test.bshoes.dto;

import lombok.Data;

import java.math.BigDecimal;

/** Request body to finalize (pay) a POS invoice. */
@Data
public class ThanhToanRequest {
    private Integer idNhanVien;
    private Integer idKhachHang;
    private Integer idPhieuGiamGia;
    private String tenNguoiNhan;
    private String soDienThoai;
    private String diaChi;
    private String phuongThucThanhToan;   // "Tiền mặt" / "Chuyển khoản" / "Thẻ"
    private String ghiChu;
    private BigDecimal phiShip;           // shipping fee for delivery orders
    private Boolean giaoHang;             // true → delivery order (status "Chờ giao"), else counter sale
}
