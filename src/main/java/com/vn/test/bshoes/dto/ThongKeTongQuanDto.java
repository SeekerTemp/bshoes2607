package com.vn.test.bshoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Dashboard summary tiles. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeTongQuanDto {
    private BigDecimal doanhThu;       // total paid revenue
    private BigDecimal loiNhuan;       // profit = revenue - cost of goods
    private BigDecimal doanhThuThang;  // paid revenue in the current calendar month
    private Integer soDon;             // total invoices
    private Integer donThanhCong;      // trang_thai = 1
    private Integer donCho;            // trang_thai = 0 (pending cart)
    private Integer donHuy;            // trang_thai = 2
    private Integer soSanPhamBan;      // units sold across paid invoices
}
