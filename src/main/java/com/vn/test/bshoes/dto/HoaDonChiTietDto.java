package com.vn.test.bshoes.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonChiTietDto {
    private Integer id;
    private Integer idSanPhamChiTiet;   // variant id — lets the POS sync its cart from the server
    private String ten;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
}
