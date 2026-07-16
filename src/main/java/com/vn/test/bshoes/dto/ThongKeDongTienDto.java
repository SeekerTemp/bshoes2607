package com.vn.test.bshoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** One month of cash flow: revenue, cost of goods, profit. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeDongTienDto {
    private Integer thang;
    private BigDecimal doanhThu;
    private BigDecimal giaVon;
    private BigDecimal loiNhuan;
}
