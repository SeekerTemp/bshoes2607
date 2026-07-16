package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonDto {
    private Integer id;
    private String ma;
    private String khach;
    private String nhanVien;
    private String ngayTao;
    private BigDecimal tongTien;
    private String trangThai;
    private List<HoaDonChiTietDto> chiTiet;

    // --- POS / receipt fields ---
    private Integer trangThaiCode;      // raw 0=Chờ, 1=Thành công, 2=Huỷ
    private String soDienThoai;
    private String diaChi;
    private String phuongThucThanhToan;
    private String ghiChu;
    private BigDecimal tongTienBanDau;   // sum of line thanhTien before voucher
    private BigDecimal tienGiamGia;      // voucher discount amount
    private BigDecimal phiShip;          // shipping fee (delivery orders)
}
