package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeSanPhamDto {
    private String maSP;
    private String tenSP;
    private String loaiSP;
    private String chatLieu;
    private String mauSac;
    private String kichThuoc;
    private Integer soLuongTon;
    private Integer soLuongBan;   // units sold across paid invoices
    private BigDecimal doanhThu;  // revenue from this variant
}
